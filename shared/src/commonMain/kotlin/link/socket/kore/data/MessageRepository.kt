package link.socket.kore.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import link.socket.kore.agents.events.Database
import link.socket.kore.agents.messages.Message
import link.socket.kore.agents.messages.MessageChannel
import link.socket.kore.agents.messages.MessageId
import link.socket.kore.agents.messages.MessageSender
import link.socket.kore.agents.messages.MessageStoreQueries
import link.socket.kore.agents.messages.MessageThread
import link.socket.kore.agents.messages.MessageThreadId
import link.socket.kore.agents.messages.MessageThreadStatus

/**
 * Repository responsible for persisting and querying Messages using SQLDelight.
 *
 * This lives in common code and works across KMP targets. Callers are responsible for
 * providing a platform-specific SQLDelight [SqlDriver] to construct the generated [link.socket.kore.agents.messages.Database]
 * instance and then pass it into this repository.
 */
class MessageRepository(
    override val json: Json,
    override val scope: CoroutineScope,
    private val database: Database,
) : Repository<MessageId, Message>(json, scope) {

    override val tag: String = "Message${super.tag}"

    private val queries: MessageStoreQueries
        get() = database.messageStoreQueries

    suspend fun saveThread(thread: MessageThread) {
        withContext(Dispatchers.IO) {
            queries.insertConversation(
                id = thread.id,
                channelId = thread.channel.getIdentifier(),
                createdById = thread.createdBy.getIdentifier(),
                status = thread.status.name,
                createdAt = thread.createdAt.toEpochMilliseconds(),
                updatedAt = thread.updatedAt.toEpochMilliseconds(),
            )
            thread.participants.forEach { participant ->
                queries.insertParticipant(
                    threadId = thread.id,
                    participantId = participant.getIdentifier(),
                )
            }
            thread.messages.forEach { message ->
                queries.insertMessage(
                    id = message.id,
                    threadId = thread.id,
                    senderId = message.sender.getIdentifier(),
                    content = message.content,
                    timestamp = message.timestamp.toEpochMilliseconds(),
                    metadata = message.metadata?.let { json.encodeToString(it) },
                )
            }
        }
    }

    suspend fun findThreadById(threadId: String): MessageThread? =
        withContext(Dispatchers.IO) {
            val messageThread = queries
                .selectMessageThreadById(threadId)
                .executeAsOneOrNull()
                ?: return@withContext null

            val participants = queries
                .selectParticipantsByThreadId(threadId)
                .executeAsList()

            val messages = queries
                .selectMessagesByThreadId(threadId)
                .executeAsList()
                .map { message ->
                    Message(
                        id = message.id,
                        threadId = message.threadId,
                        sender = MessageSender.fromSenderId(message.senderId),
                        content = message.content,
                        timestamp = Instant.fromEpochMilliseconds(message.timestamp),
                        metadata = message.metadata?.let { metadata ->
                            json.decodeFromString<Map<String, String>>(metadata)
                        },
                    )
                }

            MessageThread(
                id = messageThread.id,
                channel = MessageChannel.fromMessageChannelId(messageThread.channelId),
                createdBy = MessageSender.fromSenderId(messageThread.createdById),
                participants = participants.map { participantId ->
                    MessageSender.fromSenderId(participantId)
                },
                messages = messages,
                status = MessageThreadStatus.valueOf(messageThread.status),
                createdAt = Instant.fromEpochMilliseconds(messageThread.createdAt),
                updatedAt = Instant.fromEpochMilliseconds(messageThread.updatedAt),
            )
        }

    // TODO: Remove duplication
    suspend fun findAllThreads(): List<MessageThread> =
        withContext(Dispatchers.IO) {
            queries.selectAllMessageThreads().executeAsList().map { messageThread ->
                val participants = queries
                    .selectParticipantsByThreadId(messageThread.id)
                    .executeAsList()

                val messages = queries
                    .selectMessagesByThreadId(messageThread.id)
                    .executeAsList()
                    .map { message ->
                        Message(
                            id = message.id,
                            threadId = message.threadId,
                            sender = MessageSender.fromSenderId(message.senderId),
                            content = message.content,
                            timestamp = Instant.fromEpochMilliseconds(message.timestamp),
                            metadata = message.metadata?.let { metadata ->
                                json.decodeFromString<Map<String, String>>(metadata)
                            },
                        )
                    }

                MessageThread(
                    id = messageThread.id,
                    channel = MessageChannel.fromMessageChannelId(messageThread.channelId),
                    createdBy = MessageSender.fromSenderId(messageThread.createdById),
                    participants = participants.map { participantId ->
                        MessageSender.fromSenderId(participantId)
                    },
                    messages = messages,
                    status = MessageThreadStatus.valueOf(messageThread.status),
                    createdAt = Instant.fromEpochMilliseconds(messageThread.createdAt),
                    updatedAt = Instant.fromEpochMilliseconds(messageThread.updatedAt),
                )
            }
        }

    suspend fun addMessageToThread(threadId: MessageThreadId, message: Message) {
        withContext(Dispatchers.IO) {
            queries.insertMessage(
                id = message.id,
                threadId = threadId,
                senderId = message.sender.getIdentifier(),
                content = message.content,
                timestamp = message.timestamp.toEpochMilliseconds(),
                metadata = message.metadata?.let { metadata ->
                    json.encodeToString(metadata)
                },
            )
            // Ensure participant exists; ignore if already present (unique constraint)
            runCatching {
                queries.insertParticipant(
                    threadId = threadId,
                    participantId = message.sender.getIdentifier(),
                )
            }
            queries.updateMessageThreadStatus(
                id = threadId,
                status = MessageThreadStatus.OPEN.name, // keep status unless external change; OPEN ensures active
                updatedAt = message.timestamp.toEpochMilliseconds(),
            )
        }
    }

    suspend fun updateStatus(threadId: MessageThreadId, newStatus: MessageThreadStatus) {
        withContext(Dispatchers.IO) {
            queries.updateMessageThreadStatus(
                id = threadId,
                status = newStatus.name,
                updatedAt = Clock.System.now().toEpochMilliseconds(),
            )
        }
    }

    suspend fun delete(threadId: MessageThreadId) {
        withContext(Dispatchers.IO) {
            queries.deleteMessageThread(threadId)
        }
    }
}
