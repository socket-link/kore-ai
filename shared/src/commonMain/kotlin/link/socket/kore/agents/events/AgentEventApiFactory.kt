package link.socket.kore.agents.events

import link.socket.kore.agents.core.AgentId

/** Factory to create [AgentEventApi] instances wired to a persistent EventBus. */
class AgentEventApiFactory(
    private val eventBus: EventBus,
) {
    /**
     * Create an [AgentEventApi] for the given [agentId].
     */
    fun create(agentId: AgentId): AgentEventApi =
        AgentEventApi(
            eventBus = eventBus,
            agentId = agentId,
        )
}
