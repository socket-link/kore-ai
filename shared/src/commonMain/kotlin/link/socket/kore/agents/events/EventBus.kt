package link.socket.kore.agents.events

import kotlin.random.Random
import kotlin.reflect.KClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

typealias EventExecutionMap = MutableMap<String, suspend (Event) -> Unit>

/**
 * Token returned by [EventBus.subscribe] used to cancel a subscription via [EventBus.unsubscribe].
 */
data class SubscriptionToken(val id: String)

/**
 * Type-safe event bus for publish-subscribe communication between agents with optional persistence.
 *
 * - Thread-safe and Kotlin Multiplatform compatible
 * - Handlers are invoked asynchronously using the provided [CoroutineScope]
 * - Persistence is handled by higher-level APIs; EventBus only dispatches events to subscribers
 */
class EventBus(
    private val scope: CoroutineScope,
    private val logger: EventLogger = ConsoleEventLogger(),
) {
    // Map event KClass -> (tokenId -> handler)
    private val subscribers: MutableMap<KClass<out Event>, EventExecutionMap> = mutableMapOf()

    // Map tokenId -> event KClass (to efficiently locate the handler on unsubscribe)
    private val tokenIndex: MutableMap<String, KClass<out Event>> = mutableMapOf()

    private val mutex = Mutex()

    /**
     * Publish an [event] to all subscribers of its exact KClass.
     * - Handlers are launched asynchronously on [scope].
     * - Any individual handler failures are swallowed to avoid impacting other subscribers.
     */
    suspend fun publish(event: Event) {
        // Snapshot handlers under lock to maintain ordering and thread-safety
        val handlers: List<suspend (Event) -> Unit> = mutex.withLock {
            subscribers[event::class]?.values?.toList().orEmpty()
        }

        if (handlers.isEmpty()) {
            return
        }

        logger.logPublish(event)

        for (handler in handlers) {
            scope.launch {
                try {
                    handler(event)
                } catch (t: Throwable) {
                    // Swallow exceptions from handlers to avoid impacting other subscribers, but log them.
                    logger.logError(
                        message = "Subscriber handler failure for ${event.eventType} id=${event.eventId}",
                        throwable = t,
                    )
                }
            }
        }
    }

    /**
     * Subscribe to events of [eventClass]. Returns a [SubscriptionToken] that can be used to
     * [unsubscribe]. The [handler] runs asynchronously for each matching event.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Event> subscribe(
        eventClass: KClass<T>,
        handler: suspend (T) -> Unit
    ): SubscriptionToken {
        val token = SubscriptionToken(id = generateTokenId())
        val upcast: suspend (Event) -> Unit = { e -> handler(e as T) }

        // Register handler under lock
        runBlockingLock {
            val map: EventExecutionMap =
                subscribers.getOrPut(eventClass) { mutableMapOf() }

            map[token.id] = upcast
            tokenIndex[token.id] = eventClass
        }

        // Log subscription
        logger.logSubscription(eventClass.simpleName ?: "Event", token.id)

        return token
    }

    /**
     * Cancel the subscription associated with [token]. Safe to call multiple times.
     */
    fun unsubscribe(token: SubscriptionToken) {
        var eventClassForLog: String? = null
        runBlockingLock {
            val kClass: KClass<out Event> =
                tokenIndex.remove(token.id) ?: return@runBlockingLock

            val map: EventExecutionMap =
                subscribers[kClass] ?: return@runBlockingLock

            map.remove(token.id)

            if (map.isEmpty()) {
                subscribers.remove(kClass)
            }

            eventClassForLog = kClass.simpleName
        }

        // Log unsubscription
        logger.logSubscription("${eventClassForLog ?: "Event"}#unsubscribe", token.id)
    }

    // Helper to reuse the same locking pattern in non-suspending API without exposing Mutex.
    private inline fun <R> runBlockingLock(
        crossinline block: () -> R,
    ): R = runBlocking {
        // Fast-path tryLock not used to keep logic simple and deterministic.
        mutex.withLock { block() }
    }

    private fun generateTokenId(): String =
        Random.nextLong().toString(16)
}

/**
 * Inline reified helper for ergonomic subscriptions.
 */
inline fun <reified T : Event> EventBus.subscribe(
    noinline handler: suspend (T) -> Unit,
): SubscriptionToken = subscribe(
    eventClass = T::class,
    handler = handler,
)
