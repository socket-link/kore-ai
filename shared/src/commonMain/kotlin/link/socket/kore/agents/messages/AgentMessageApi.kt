package link.socket.kore.agents.messages

import kotlinx.datetime.Clock
import link.socket.kore.agents.core.AgentId
import link.socket.kore.agents.events.EventBus
import link.socket.kore.agents.events.EventSource
import link.socket.kore.agents.events.MessageEvent
import link.socket.kore.data.MessageRepository
import link.socket.kore.util.randomUUID

/**
 * Service layer that orchestrates message thread operations, enforces business rules,
 * persists state via MessageRepository, and publishes domain events via EventBus.
 */
class AgentMessageApi(
    private val repository: MessageRepository,
    private val eventBus: EventBus,
    val agentId: AgentId,
) {

    /** Create a new message thread with an initial message and publish events. */
    suspend fun createThread(
        participants: List<MessageSenderId>,
        channel: MessageChannel,
        initialMessageContent: String,
    ): MessageThread {
        val now = Clock.System.now()
        val threadId = randomUUID()

        val sender = MessageSender.fromSenderId(agentId)
        val message = Message(
            id = randomUUID(),
            threadId = threadId,
            sender = sender,
            content = initialMessageContent,
            timestamp = now,
            metadata = null,
        )

        // create base thread from initial message
        var thread = MessageThread.create(
            id = threadId,
            channel = channel,
            initialMessage = message,
        )

        // merge provided participants (if any)
        if (participants.isNotEmpty()) {
            val extraParticipants = participants.map { MessageSender.fromSenderId(it) }
            val merged = (thread.participants + extraParticipants).distinctBy { it.getIdentifier() }
            thread = thread.copy(participants = merged)
        }

        repository.saveThread(thread)

        // Publish creation and initial message posted events
        eventBus.publish(
            MessageEvent.ThreadCreated(
                eventId = randomUUID(),
                thread = thread,
            ),
        )

        eventBus.publish(
            MessageEvent.MessagePosted(
                eventId = randomUUID(),
                messageThreadId = thread.id,
                channel = thread.channel,
                message = message,
            ),
        )

        return thread
    }

    /** Post a message to an existing thread. Throws if thread is blocked waiting for human. */
    suspend fun postMessage(
        threadId: MessageThreadId,
        content: String,
    ): Message {
        val thread = repository.findThreadById(threadId)
            ?: throw IllegalArgumentException("Thread not found: $threadId")

        require(thread.status != MessageThreadStatus.WAITING_FOR_HUMAN) {
            "Cannot post message while thread is waiting for human intervention"
        }

        val now = Clock.System.now()
        val message = Message(
            id = randomUUID(),
            threadId = threadId,
            sender = MessageSender.fromSenderId(agentId),
            content = content,
            timestamp = now,
            metadata = null,
        )

        repository.addMessageToThread(threadId, message)

        eventBus.publish(
            MessageEvent.MessagePosted(
                eventId = randomUUID(),
                messageThreadId = threadId,
                channel = thread.channel,
                message = message,
            ),
        )

        return message
    }

    /** Escalate a thread to human, blocking further agent activity. */
    suspend fun escalateToHuman(
        threadId: MessageThreadId,
        reason: String,
        context: Map<String, String> = emptyMap(),
    ) {
        val thread = repository.findThreadById(threadId)
            ?: throw IllegalArgumentException("Thread not found: $threadId")

        require(thread.status != MessageThreadStatus.RESOLVED) {
            "Cannot escalate a resolved thread"
        }

        val old = thread.status
        val newStatus = MessageThreadStatus.WAITING_FOR_HUMAN

        repository.updateStatus(threadId, newStatus)

        val now = Clock.System.now()
        eventBus.publish(
            MessageEvent.EscalationRequested(
                eventId = randomUUID(),
                timestamp = now,
                eventSource = EventSource.Agent(agentId),
                messageThreadId = threadId,
                reason = reason,
                context = context,
            ),
        )

        eventBus.publish(
            MessageEvent.ThreadStatusChanged(
                eventId = randomUUID(),
                timestamp = now,
                eventSource = EventSource.Agent(agentId),
                messageThreadId = threadId,
                oldStatus = old,
                newStatus = newStatus,
            ),
        )
    }

    /** Resolve an escalated thread. */
    suspend fun resolveThread(
        threadId: MessageThreadId,
    ) {
        val thread = repository.findThreadById(threadId)
            ?: throw IllegalArgumentException("Thread not found: $threadId")

        val old = thread.status
        val newStatus = MessageThreadStatus.RESOLVED

        repository.updateStatus(threadId, newStatus)

        eventBus.publish(
            MessageEvent.ThreadStatusChanged(
                eventId = randomUUID(),
                timestamp = Clock.System.now(),
                eventSource = EventSource.Agent(agentId),
                messageThreadId = threadId,
                oldStatus = old,
                newStatus = newStatus,
            ),
        )
    }

    /** Retrieve a thread by id. */
    suspend fun getThread(threadId: MessageThreadId): MessageThread? =
        repository.findThreadById(threadId)

    /** List all threads. */
    suspend fun getAllThreads(): List<MessageThread> =
        repository.findAllThreads()
}
