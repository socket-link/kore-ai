package link.socket.kore.agents.events

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.Clock
import link.socket.kore.agents.conversation.model.Channel
import link.socket.kore.agents.conversation.model.Message
import link.socket.kore.agents.conversation.model.Sender
import link.socket.kore.agents.conversation.model.Thread
import link.socket.kore.agents.conversation.model.ThreadStatus
import link.socket.kore.agents.conversation.model.toEventSource

class ConversationEventTest {

    private val stubSenderA = Sender.Agent(agentId = "agentA")
    private val stubSenderB = Sender.Agent(agentId = "agentB")
    private val stubThreadId = "t1"
    private val stubChannel = Channel.Public.Engineering

    @Test
    fun `can instantiate all conversation events and access properties`() {
        val now = Clock.System.now()

        val initialMessage = Message(
            id = "m1",
            sender = stubSenderA,
            content = "Hello",
            timestamp = now,
            metadata = mapOf("k" to "v"),
        )

        val created = ConversationEvent.ThreadCreated(
            eventId = "11111111-1111-1111-1111-111111111111",
            thread = Thread.create(stubThreadId, stubChannel, initialMessage),
        )

        val followupMessage = Message(
            id = "m2",
            sender = stubSenderB,
            content = "Hi there",
            timestamp = now + 1.seconds,
        )

        val posted = ConversationEvent.MessagePosted(
            eventId = "22222222-2222-2222-2222-222222222222",
            threadId = stubThreadId,
            channel = stubChannel,
            message = followupMessage,
        )

        val statusChanged = ConversationEvent.ThreadStatusChanged(
            eventId = "33333333-3333-3333-3333-333333333333",
            timestamp = now + 2.seconds,
            eventSource = stubSenderA.toEventSource(),
            threadId = stubThreadId,
            oldStatus = ThreadStatus.OPEN,
            newStatus = ThreadStatus.WAITING_FOR_HUMAN,
        )

        val escalation = ConversationEvent.EscalationRequested(
            eventId = "44444444-4444-4444-4444-444444444444",
            timestamp = now + 3.seconds,
            eventSource = stubSenderA.toEventSource(),
            threadId = stubThreadId,
            reason = "Needs human input",
            context = mapOf("priority" to "high"),
        )

        // Basic property checks
        assertEquals("t1", created.threadId)
        assertEquals(initialMessage, created.thread.messages.first())
        assertEquals(Channel.Public.Engineering, posted.channel)
        assertEquals(ThreadStatus.OPEN, statusChanged.oldStatus)
        assertEquals("agentA", escalation.eventSource.getIdentifier())

        // Exhaustive when expression over sealed interface
        fun handle(event: ConversationEvent): String = when (event) {
            is ConversationEvent.ThreadCreated -> "created"
            is ConversationEvent.MessagePosted -> "posted"
            is ConversationEvent.ThreadStatusChanged -> "status"
            is ConversationEvent.EscalationRequested -> "escalation"
        }

        assertEquals("created", handle(created))
        assertEquals("posted", handle(posted))
        assertEquals("status", handle(statusChanged))
        assertEquals("escalation", handle(escalation))
    }
}
