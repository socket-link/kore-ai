package link.socket.kore.agents.events

import kotlinx.coroutines.CoroutineScope
import link.socket.kore.data.EventRepository

/**
 * Factory for creating a fully wired [EventBus] with persistence using SQLDelight.
 */
class EventBusFactory(
    private val scope: CoroutineScope,
    private val logger: EventLogger = ConsoleEventLogger(),
) {
    /**
     * Create a new [EventBus] backed by an existing [EventRepository].
     *
     * - [eventRepository] is the repository that handles event persistence and retrieval
     */
    fun create(
        eventRepository: EventRepository,
    ): EventBus = EventBus(
        scope = scope,
        eventRepository = eventRepository,
        logger = logger,
    )
}
