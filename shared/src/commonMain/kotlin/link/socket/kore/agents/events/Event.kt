package link.socket.kore.agents.events

import kotlinx.serialization.Serializable

typealias EventId = String

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

    /** Epoch milliseconds when the event occurred. */
    val timestamp: Long

    /** Identifier of the agent that produced the event. */
    val sourceAgentId: String

    /**
     * A type discriminator for the event, derived from the implementing class name.
     * Implementations inherit this default, but may override to customize.
     */
    val eventType: String
        get() = this::class.simpleName ?: "Event"
}
