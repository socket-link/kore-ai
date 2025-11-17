package link.socket.kore.agents.messages

import kotlinx.datetime.Clock
import link.socket.kore.agents.core.AgentId
import link.socket.kore.agents.events.ConsoleEventLogger
import link.socket.kore.agents.events.EventBus
import link.socket.kore.agents.events.EventLogger
import link.socket.kore.agents.events.EventSource
import link.socket.kore.agents.events.MessageEvent
import link.socket.kore.data.MessageRepository
import link.socket.kore.util.randomUUID

/**
 * Service layer that orchestrates message thread operations, enforces business rules,
 * persists state via MessageRepository, and publishes domain events via EventBus.
 */
class AgentMessageApi(
    val agentId: AgentId,
    private val messageRepository: MessageRepository,
    private val eventBus: EventBus,
    private val logger: EventLogger = ConsoleEventLogger(),
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

        // create base thread from the initial message
        var thread = MessageThread.create(
            id = threadId,
            channel = channel,
            initialMessage = message,
        )

        // merge provided participants (if any)
        if (participants.isNotEmpty()) {
            val extraParticipants = participants.map { participant ->
                MessageSender.fromSenderId(participant)
            }
            val merged = (thread.participants + extraParticipants).distinctBy { it.getIdentifier() }
            thread = thread.copy(participants = merged)
        }

        messageRepository
            .saveThread(thread)
            .onSuccess {
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
            }
            .onFailure { throwable ->
                logger.logError(
                    message = "Failed to create thread in channel ${thread.channel} from $sender",
                    throwable = throwable,
                )
            }

        return thread
    }

    /** Post a message to an existing thread. Throws if thread is blocked waiting for human. */
    suspend fun postMessage(
        threadId: MessageThreadId,
        content: String,
    ): Message {
        val thread = messageRepository
            .findThreadById(threadId)
            .onFailure { throwable ->
                logger.logError(
                    message = "Failed to find thread with id $threadId",
                    throwable = throwable,
                )
                throw IllegalArgumentException("Thread not found: $threadId")
            }
            .getOrNull()

        requireNotNull(thread)
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

        messageRepository
            .addMessageToThread(threadId, message)
            .onSuccess {
                eventBus.publish(
                    MessageEvent.MessagePosted(
                        eventId = randomUUID(),
                        messageThreadId = threadId,
                        channel = thread.channel,
                        message = message,
                    ),
                )
            }
            .onFailure { throwable ->
                logger.logError(
                    message = "Failed to add message to thread with id $threadId",
                    throwable = throwable,
                )
            }

        return message
    }

    /** Escalate a thread to human, blocking further agent activity. */
    suspend fun escalateToHuman(
        threadId: MessageThreadId,
        reason: String,
        context: Map<String, String> = emptyMap(),
    ) {
        val thread = messageRepository
            .findThreadById(threadId)
            .onFailure { throwable ->
                logger.logError(
                    message = "Failed to find thread with id $threadId",
                    throwable = throwable,
                )
                throw IllegalArgumentException("Thread not found: $threadId")
            }
            .getOrNull()

        requireNotNull(thread)
        require(thread.status != MessageThreadStatus.RESOLVED) {
            "Cannot escalate a resolved thread"
        }

        val oldStatus = thread.status
        val newStatus = MessageThreadStatus.WAITING_FOR_HUMAN

        messageRepository
            .updateStatus(threadId, newStatus)
            .onSuccess {
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
                        oldStatus = oldStatus,
                        newStatus = newStatus,
                    ),
                )
            }
            .onFailure { throwable ->
                logger.logError(
                    message = "Failed to update thread status to $newStatus for thread with id $threadId",
                    throwable = throwable,
                )
            }
    }

    /** Resolve an escalated thread. */
    suspend fun resolveThread(
        threadId: MessageThreadId,
    ) {
        val thread = messageRepository
            .findThreadById(threadId)
            .onFailure { throwable ->
                logger.logError(
                    message = "Failed to find thread with id $threadId",
                    throwable = throwable,
                )
                throw IllegalArgumentException("Thread not found: $threadId")
            }
            .getOrNull()

        requireNotNull(thread)
        val oldStatus = thread.status
        val newStatus = MessageThreadStatus.RESOLVED

        messageRepository
            .updateStatus(threadId, newStatus)
            .onSuccess {
                eventBus.publish(
                    MessageEvent.ThreadStatusChanged(
                        eventId = randomUUID(),
                        timestamp = Clock.System.now(),
                        eventSource = EventSource.Agent(agentId),
                        messageThreadId = threadId,
                        oldStatus = oldStatus,
                        newStatus = newStatus,
                    ),
                )
            }
            .onFailure { throwable ->
                logger.logError(
                    message = "Failed to update thread status to $newStatus for thread with id $threadId",
                    throwable = throwable,
                )
            }
    }

    /** Retrieve a thread by id. */
    suspend fun getThread(threadId: MessageThreadId): Result<MessageThread> =
        messageRepository.findThreadById(threadId)

    /** List all threads. */
    suspend fun getAllThreads(): Result<List<MessageThread>> =
        messageRepository.findAllThreads()
}
