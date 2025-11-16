package link.socket.kore.agents.conversation.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

typealias ThreadId = String

@Serializable
data class Thread(
    val id: ThreadId,
    val channel: Channel,
    val createdBy: Sender,
    val participants: List<Sender>,
    val messages: List<Message>,
    val status: ThreadStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    // Function to create a new conversation thread with an initial message
    companion object Companion {
        fun create(
            id: String,
            channel: Channel,
            initialMessage: Message,
        ): Thread {
            val now = initialMessage.timestamp
            val initialParticipants = listOf(initialMessage.sender)

            return Thread(
                id = id,
                channel = channel,
                createdBy = initialMessage.sender,
                participants = initialParticipants,
                messages = listOf(initialMessage),
                status = ThreadStatus.OPEN,
                createdAt = now,
                updatedAt = now,
            )
        }
    }

    // Function to add a message and return the updated conversation
    fun addMessage(message: Message): Thread {
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
    fun updateStatus(newStatus: ThreadStatus): Thread {
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
