package link.socket.kore.agents.events.subscription

import kotlinx.serialization.Serializable
import link.socket.kore.agents.core.AgentId
import link.socket.kore.agents.events.EventClassType

/**
 * Subscription returned by [link.socket.kore.agents.events.bus.EventBus.subscribe] used to cancel a subscription via [link.socket.kore.agents.events.bus.EventBus.unsubscribe].
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
