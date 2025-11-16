package link.socket.kore.agents.conversation.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class ConversationModelsTest {

    @Test
    fun `can create conversation with initial message`() {
        val ts: Instant = Clock.System.now()
        val msg = Message(
            id = "m1",
            sender = Sender.Agent("agent-1"),
            content = "Hello",
            timestamp = ts,
        )

        val convo = Thread.create(
            id = "c1",
            channel = Channel.Public.Engineering,
            initialMessage = msg,
        )

        assertEquals("c1", convo.id)
        assertEquals(listOf(msg), convo.messages)
        assertEquals(ThreadStatus.OPEN, convo.status)
        assertEquals(ts, convo.createdAt)
        assertEquals(ts, convo.updatedAt)
        assertEquals("#engineering", convo.channel.toDisplayString())
    }

    @Test
    fun `ThreadStatus prevents invalid transition from RESOLVED to OPEN`() {
        val resolvedToOpen = ThreadStatus.RESOLVED.canTransitionTo(ThreadStatus.OPEN)
        assertEquals(false, resolvedToOpen)

        val openToResolved = ThreadStatus.OPEN.canTransitionTo(ThreadStatus.RESOLVED)
        assertEquals(true, openToResolved)
    }

    @Test
    fun `addMessage returns new instance with appended message`() {
        val t1 = Clock.System.now()
        val t2 = t1 + 1.seconds

        val m1 = Message(
            id = "m1",
            sender = Sender.Agent("agent-1"),
            content = "Start",
            timestamp = t1,
        )

        val convo1 = Thread.create(
            id = "c1",
            channel = Channel.Public.Product,
            initialMessage = m1,
        )

        val m2 = Message(
            id = "m2",
            sender = Sender.Agent("agent-2"),
            content = "Reply",
            timestamp = t2,
        )

        val convo2 = convo1.addMessage(m2)

        // original remains unchanged
        assertEquals(listOf(m1), convo1.messages)

        // new has both messages
        assertEquals(listOf(m1, m2), convo2.messages)
        assertEquals(t2, convo2.updatedAt)
    }
}
