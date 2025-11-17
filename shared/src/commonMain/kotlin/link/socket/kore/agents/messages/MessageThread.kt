package link.socket.kore.agents.messages

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

typealias MessageThreadId = String

@Serializable
data class MessageThread(
    val id: MessageThreadId,
    val channel: MessageChannel,
    val createdBy: MessageSender,
    val participants: List<MessageSender>,
    val messages: List<Message>,
    val status: MessageThreadStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    // Function to create a new conversation thread with an initial message
    companion object Companion {
        fun create(
            id: String,
            channel: MessageChannel,
            initialMessage: Message,
        ): MessageThread {
            val now = initialMessage.timestamp
            val initialParticipants = listOf(initialMessage.sender)

            return MessageThread(
                id = id,
                channel = channel,
                createdBy = initialMessage.sender,
                participants = initialParticipants,
                messages = listOf(initialMessage),
                status = MessageThreadStatus.OPEN,
                createdAt = now,
                updatedAt = now,
            )
        }
    }

    // Function to add a message and return the updated conversation
    fun addMessage(message: Message): MessageThread {
        val updatedParticipants = if (message.sender !in participants) {
            participants + message.sender
        } else {
            participants
        }

        return copy(
            participants = updatedParticipants,
            messages = this.messages + message,
            updatedAt = message.timestamp,
        )
    }

    // Function to update status after first checking validation
    fun updateStatus(newStatus: MessageThreadStatus): MessageThread {
        val validStatusTransition = status.canTransitionTo(newStatus)

        require(validStatusTransition) {
            "Invalid status transition from ${this.status} to $newStatus"
        }

        return copy(
            status = newStatus,
            updatedAt = Clock.System.now(),
        )
    }
}
