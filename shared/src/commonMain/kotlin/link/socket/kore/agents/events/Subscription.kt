package link.socket.kore.agents.events

import kotlinx.serialization.Serializable

@Serializable
sealed interface Subscription {
    val subscriptionId: String
}
