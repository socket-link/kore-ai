package link.socket.kore.agents.events.subscription

import kotlinx.serialization.Serializable

@Serializable
sealed interface Subscription {
    val subscriptionId: String
}
