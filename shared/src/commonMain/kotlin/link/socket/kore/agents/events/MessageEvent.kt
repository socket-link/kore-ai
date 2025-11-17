package link.socket.kore.agents.events

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import link.socket.kore.agents.messages.Message
import link.socket.kore.agents.messages.MessageChannel
import link.socket.kore.agents.messages.MessageThread
import link.socket.kore.agents.messages.MessageThreadId
import link.socket.kore.agents.messages.MessageThreadStatus
import link.socket.kore.agents.messages.toEventSource

/** Base sealed interface for type-safe event handling. */
sealed interface MessageEvent : Event {

    val messageThreadId: MessageThreadId

    @Serializable
    data class ThreadCreated(
        override val eventId: EventId,
        val thread: MessageThread,
    ) : MessageEvent {

        override val eventType: String = EVENT_TYPE
        override val eventSource: EventSource = thread.createdBy.toEventSource()
        override val messageThreadId: MessageThreadId = thread.id
        override val timestamp: Instant = thread.createdAt

        companion object {
            const val EVENT_TYPE = "ThreadCreated"
        }
    }

    data class MessagePosted(
        override val eventId: EventId,
        override val messageThreadId: MessageThreadId,
        val channel: MessageChannel,
        val message: Message,
    ) : MessageEvent {

        override val eventType: String = EVENT_TYPE
        override val eventSource: EventSource = message.sender.toEventSource()
        override val timestamp: Instant = message.timestamp

        companion object {
            const val EVENT_TYPE = "MessagePosted"
        }
    }

    data class ThreadStatusChanged(
        override val eventId: EventId,
        override val timestamp: Instant,
        override val eventSource: EventSource,
        override val messageThreadId: MessageThreadId,
        val oldStatus: MessageThreadStatus,
        val newStatus: MessageThreadStatus,
    ) : MessageEvent {

        override val eventType: String = EVENT_TYPE

        companion object {
            const val EVENT_TYPE = "ThreadStatusChanged"
        }
    }

    data class EscalationRequested(
        override val eventId: EventId,
        override val timestamp: Instant,
        override val eventSource: EventSource,
        override val messageThreadId: MessageThreadId,
        val reason: String,
        val context: Map<String, String> = emptyMap(),
    ) : MessageEvent {

        override val eventType: String = EVENT_TYPE

        companion object {
            const val EVENT_TYPE = "EscalationRequested"
        }
    }
}
