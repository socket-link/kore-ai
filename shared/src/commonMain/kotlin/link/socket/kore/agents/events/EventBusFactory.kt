package link.socket.kore.agents.events

import kotlinx.coroutines.CoroutineScope

/**
 * Factory for creating an [EventBus]. Persistence is handled by higher-level APIs.
 */
class EventBusFactory(
    private val scope: CoroutineScope,
    private val logger: EventLogger = ConsoleEventLogger(),
) {
    /**
     * Create a new [EventBus].
     */
    fun create(): EventBus = EventBus(
        scope = scope,
        logger = logger,
    )
}
