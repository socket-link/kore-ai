package link.socket.kore.agents.events

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import link.socket.kore.agents.conversation.model.Channel
import link.socket.kore.agents.conversation.model.Message
import link.socket.kore.agents.conversation.model.Thread
import link.socket.kore.agents.conversation.model.ThreadId
import link.socket.kore.agents.conversation.model.ThreadStatus
import link.socket.kore.agents.conversation.model.toEventSource

/** Base sealed interface for type-safe event handling. */
sealed interface ConversationEvent : Event {

    val threadId: ThreadId

    @Serializable
    data class ThreadCreated(
        override val eventId: EventId,
        val thread: Thread,
        override val eventSource: EventSource = thread.createdBy.toEventSource(),
        override val threadId: ThreadId = thread.id,
        override val timestamp: Instant = thread.createdAt,
    ) : ConversationEvent

    data class MessagePosted(
        override val eventId: EventId,
        override val threadId: ThreadId,
        val channel: Channel,
        val message: Message,
        override val eventSource: EventSource = message.sender.toEventSource(),
        override val timestamp: Instant = message.timestamp,
    ) : ConversationEvent

    data class ThreadStatusChanged(
        override val eventId: EventId,
        override val timestamp: Instant,
        override val eventSource: EventSource,
        override val threadId: ThreadId,
        val oldStatus: ThreadStatus,
        val newStatus: ThreadStatus,
    ) : ConversationEvent

    data class EscalationRequested(
        override val eventId: EventId,
        override val timestamp: Instant,
        override val eventSource: EventSource,
        override val threadId: ThreadId,
        val reason: String,
        val context: Map<String, String> = emptyMap(),
    ) : ConversationEvent
}
