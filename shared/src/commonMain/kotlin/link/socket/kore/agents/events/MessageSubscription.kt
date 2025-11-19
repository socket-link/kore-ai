package link.socket.kore.agents.events

import kotlinx.serialization.Serializable
import link.socket.kore.agents.core.AgentId
import link.socket.kore.agents.events.messages.MessageChannel
import link.socket.kore.agents.events.messages.MessageThreadId

/**
 * Subscription returned by [EventBus.subscribe] used to cancel a subscription via [EventBus.unsubscribe].
 */
@Serializable
sealed class MessageSubscription(
    open val agentId: AgentId,
) : Subscription {

    @Serializable
    data class ByChannels(
        val agentIdOverride: AgentId,
        val channels: Set<MessageChannel>,
    ) : MessageSubscription(agentIdOverride) {

        override val subscriptionId: String =
            channels.joinToString(",") + "/$agentId"
    }

    @Serializable
    data class ByThreads(
        val agentIdOverride: AgentId,
        val threadIds: Set<MessageThreadId>,
    ) : MessageSubscription(agentIdOverride) {

        override val subscriptionId: String =
            threadIds.joinToString(",") + "/$agentId"
    }
}
