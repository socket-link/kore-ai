package link.socket.kore.agents.conversation.model

import kotlinx.datetime.Instant
import link.socket.kore.agents.core.AgentId

typealias MessageId = String

sealed class Sender {
    data class Agent(val agentId: AgentId) : Sender()
    object Human : Sender()
}

data class Message(
    val id: MessageId,
    val sender: Sender,
    val content: String,
    val timestamp: Instant,
    val metadata: Map<String, String> = emptyMap(),
)
