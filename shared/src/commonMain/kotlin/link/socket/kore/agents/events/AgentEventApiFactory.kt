package link.socket.kore.agents.events

import link.socket.kore.data.EventRepository

/** Factory to create [AgentEventApi] instances wired to a persistent EventBus. */
class AgentEventApiFactory(
    private val eventRepository: EventRepository,
    private val eventBusFactory: EventBusFactory,
) {
    // Use a single shared EventBus instance so all AgentEventApi objects communicate
    // over the same bus (same in-memory pub/sub), while still persisting via the
    // provided EventRepository.
    private val sharedEventBus: EventBus by lazy {
        eventBusFactory.create(eventRepository)
    }
    /**
     * Create an [AgentEventApi] for the given [agentId] using the provided SQLDelight [databaseDriver].
     */
    fun create(
        agentId: String,
    ): AgentEventApi {
        return AgentEventApi(
            eventBus = sharedEventBus,
            agentId = agentId,
        )
    }
}
