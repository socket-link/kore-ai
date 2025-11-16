package link.socket.kore.agents.events

import kotlinx.serialization.Serializable

/** Urgency levels for questions raised by agents. */
@Serializable
enum class Urgency {
    LOW,
    MEDIUM,
    HIGH;
}

/** Event emitted when a new task is created in the system. */
@Serializable
data class TaskCreatedEvent(
    override val eventId: String,
    override val timestamp: Long,
    override val sourceAgentId: String,
    val taskId: String,
    val description: String,
    val assignedTo: String?,
) : Event

/** Event emitted when an agent raises a question needing attention. */
@Serializable
data class QuestionRaisedEvent(
    override val eventId: String,
    override val timestamp: Long,
    override val sourceAgentId: String,
    val questionText: String,
    val context: String,
    val urgency: Urgency,
) : Event

/** Event emitted when code is submitted by an agent for review or integration. */
@Serializable
data class CodeSubmittedEvent(
    override val eventId: String,
    override val timestamp: Long,
    override val sourceAgentId: String,
    val filePath: String,
    val changeDescription: String,
    val reviewRequired: Boolean,
) : Event
