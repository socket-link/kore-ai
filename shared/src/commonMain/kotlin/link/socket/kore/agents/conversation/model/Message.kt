package link.socket.kore.agents.conversation.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import link.socket.kore.agents.core.AgentId
import link.socket.kore.agents.events.EventSource

typealias MessageId = String

@Serializable
sealed class Sender {
    data class Agent(val agentId: AgentId) : Sender()
    data object Human : Sender()

    fun getIdentifier(): String = when (this) {
        is Agent -> agentId
        is Human -> "human"
    }
}

@Serializable
data class Message(
    val id: MessageId,
    val sender: Sender,
    val content: String,
    val timestamp: Instant,
    val metadata: Map<String, String> = emptyMap(),
)

fun Sender.toEventSource(): EventSource =
    when (this) {
        is Sender.Agent -> EventSource.Agent(agentId)
        Sender.Human -> EventSource.Human
    }
