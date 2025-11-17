package link.socket.kore.agents.events

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import link.socket.kore.agents.core.AgentId

typealias EventId = String

/** Urgency levels for questions raised by agents. */
@Serializable
enum class Urgency {
    LOW,
    MEDIUM,
    HIGH;
}

/** Source of an event, either an agent or a human. */
@Serializable
sealed class EventSource {

    @Serializable
    data class Agent(val agentId: AgentId) : EventSource()

    @Serializable
    data object Human : EventSource()

    fun getIdentifier(): String = when (this) {
        is Agent -> agentId
        is Human -> "human"
    }
}

/**
 * Base type for all events flowing through the agent system.
 *
 * This sealed hierarchy enables type-safe event definitions across KMP targets
 * and supports kotlinx.serialization for persistence and transport.
 */
@Serializable
sealed interface Event {

    /** Globally unique identifier for this event (UUID string). */
    val eventId: EventId

    /** [kotlinx.datetime.Instant] when the event occurred. */
    val timestamp: Instant

    /** Identifier of the agent or human that produced the event. */
    val eventSource: EventSource

    /**
     * A type discriminator for the event.
     */
    val eventType: String

    /** Event emitted when a new task is created in the system. */
    @Serializable
    data class TaskCreated(
        override val eventId: EventId,
        override val timestamp: Instant,
        override val eventSource: EventSource,
        val taskId: String,
        val description: String,
        val assignedTo: AgentId?,
    ) : Event {

        override val eventType: String = EVENT_TYPE

        companion object {
            const val EVENT_TYPE = "TaskCreated"
        }
    }

    /** Event emitted when an agent raises a question needing attention. */
    @Serializable
    data class QuestionRaised(
        override val eventId: EventId,
        override val timestamp: Instant,
        override val eventSource: EventSource,
        val questionText: String,
        val context: String,
        val urgency: Urgency,
    ) : Event {

        override val eventType: String = EVENT_TYPE

        companion object {
            const val EVENT_TYPE = "QuestionRaised"
        }
    }

    /** Event emitted when code is submitted by an agent for review or integration. */
    @Serializable
    data class CodeSubmitted(
        override val eventId: EventId,
        override val timestamp: Instant,
        override val eventSource: EventSource,
        val filePath: String,
        val changeDescription: String,
        val reviewRequired: Boolean,
    ) : Event {

        override val eventType: String = EVENT_TYPE

        companion object {
            const val EVENT_TYPE = "CodeSubmitted"
        }
    }
}
