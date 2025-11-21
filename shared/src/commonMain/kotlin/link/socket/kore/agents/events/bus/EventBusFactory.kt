package link.socket.kore.agents.events.bus

import kotlinx.coroutines.CoroutineScope
import link.socket.kore.agents.events.utils.ConsoleEventLogger
import link.socket.kore.agents.events.utils.EventLogger

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
