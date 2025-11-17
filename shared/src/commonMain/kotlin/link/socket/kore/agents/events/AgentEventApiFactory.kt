package link.socket.kore.agents.events

import link.socket.kore.agents.core.AgentId
import link.socket.kore.data.EventRepository

/** Factory to create [AgentEventApi] instances wired to a persistent EventBus. */
class AgentEventApiFactory(
    private val eventRepository: EventRepository,
    private val eventBus: EventBus,
    private val logger: EventLogger = ConsoleEventLogger(),
) {
    /**
     * Create an [AgentEventApi] for the given [agentId].
     */
    fun create(agentId: AgentId): AgentEventApi =
        AgentEventApi(
            agentId = agentId,
            eventRepository = eventRepository,
            eventBus = eventBus,
            logger = logger,
        )
}
