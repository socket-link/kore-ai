package link.socket.kore.agents.events

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import link.socket.kore.agents.core.AgentId

typealias HandlerMap = MutableMap<EventClassType, List<EventHandler<Event, Subscription>>>
typealias SubscriptionMap = MutableMap<EventClassType, Subscription>

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
    // Map event from EventClassType -> (subscriptionId, eventHandler)
    private val handlerMap: HandlerMap = mutableMapOf()

    // Map from subscriptionId -> EventClassType (to efficiently locate the handler on unsubscribe)
    private val subscriptionMap: SubscriptionMap = mutableMapOf()

    // Map from EventClassType -> active handler Jobs for cancellation on unsubscribe
    private val activeJobs: MutableMap<EventClassType, MutableList<Job>> = mutableMapOf()

    private val mutex = Mutex()

    /**
     * Publish an [event] to all subscribers of its exact KClass.
     * - Handlers are launched asynchronously on [scope].
     * - Any individual handler failures are swallowed to avoid impacting other subscribers.
     */
    suspend fun publish(event: Event) {
        // Snapshot handlers under lock to maintain ordering and thread-safety
        val handlers: List<EventHandler<Event, Subscription>> = mutex.withLock {
            handlerMap[event.eventClassType].orEmpty()
        }

        if (handlers.isEmpty()) {
            return
        }

        logger.logPublish(event)

        for (handler in handlers) {
            val job = scope.launch {
                try {
                    val subscription = subscriptionMap[event.eventClassType]
                    handler.execute(event, subscription)
                } catch (throwable: Throwable) {
                    // Swallow exceptions from handlers to avoid impacting other subscribers, but still log them.
                    logger.logError(
                        message = "Subscriber handler failure for ${event.eventClassType}(id=${event.eventId})",
                        throwable = throwable,
                    )
                }
            }

            // Track job for potential cancellation on unsubscribe
            mutex.withLock {
                activeJobs.getOrPut(event.eventClassType) { mutableListOf() }.add(job)
            }

            // Clean up completed jobs
            job.invokeOnCompletion {
                runBlocking {
                    mutex.withLock {
                        activeJobs[event.eventClassType]?.remove(job)
                    }
                }
            }
        }
    }

    /**
     * Subscribe to events of [eventClassType]. Returns a [EventSubscription] that can be used to
     * [unsubscribe]. The [handler] runs asynchronously for each matching event.
     */
    @Suppress("UNCHECKED_CAST")
    fun subscribe(
        agentId: AgentId,
        eventClassType: EventClassType,
        handler: EventHandler<Event, Subscription>,
    ): Subscription {
        val subscription = EventSubscription.ByEventClassType(
            agentIdOverride = agentId,
            eventClassTypes = setOf(eventClassType),
        )

        val eventHandler: EventHandler<Event, Subscription> = EventHandler { event, subscription ->
            handler.execute(event, subscription)
        }

        // Register handler under lock
        runBlockingLock {
            val existing = handlerMap[eventClassType]
            val updated = if (existing == null) listOf(eventHandler) else existing + eventHandler
            handlerMap[eventClassType] = updated
            subscriptionMap.getOrPut(eventClassType) { subscription }
        }

        // Log subscription
        logger.logSubscription(eventClassType, subscription)

        return subscription
    }

    fun unsubscribe(eventClassType: EventClassType) {
        runBlockingLock {
            val subscription = subscriptionMap[eventClassType] ?: return@runBlockingLock

            // Cancel any in-flight handlers for this subscription
            activeJobs[eventClassType]?.forEach { job ->
                job.cancel()
            }
            activeJobs.remove(eventClassType)

            subscriptionMap.remove(eventClassType)
            handlerMap.remove(eventClassType)

            // Log unsubscription
            logger.logUnsubscription(eventClassType, subscription)
        }
    }

    // Helper to reuse the same locking pattern in non-suspending API without exposing Mutex.
    private inline fun <R> runBlockingLock(
        crossinline block: () -> R,
    ): R = runBlocking {
        // Fast-path tryLock not used to keep logic simple and deterministic.
        mutex.withLock { block() }
    }
}

/**
 * Inline reified helper for ergonomic subscriptions.
 */
inline fun <reified E : Event, reified S : Subscription> EventBus.subscribe(
    agentId: AgentId,
    eventClassType: EventClassType,
    noinline handler: suspend (E, S?) -> Unit,
): Subscription = subscribe(
    agentId = agentId,
    eventClassType = eventClassType,
    handler = EventHandler { event, subscription ->
        handler(event as E, subscription as S?)
    },
)
