package link.socket.kore.agents.events

import kotlinx.serialization.Serializable
import link.socket.kore.agents.core.AgentId

/**
 * Subscription returned by [EventBus.subscribe] used to cancel a subscription via [EventBus.unsubscribe].
 */
@Serializable
sealed class EventSubscription(
    open val agentId: AgentId,
) : Subscription {

    @Serializable
    data class ByEventClassType(
        val agentIdOverride: AgentId,
        val eventClassTypes: Set<EventClassType>,
    ) : EventSubscription(agentIdOverride) {

        override val subscriptionId: String =
            eventClassTypes.joinToString(",") + "/$agentId"
    }
}
