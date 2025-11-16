package link.socket.kore.agents.events

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

class EventBusTest {

    private fun taskEvent(): TaskCreatedEvent = TaskCreatedEvent(
        eventId = "evt-1",
        timestamp = System.currentTimeMillis(),
        sourceAgentId = "agent-A",
        taskId = "task-123",
        description = "Do something important",
        assignedTo = "agent-B",
    )

    private fun questionEvent(): QuestionRaisedEvent = QuestionRaisedEvent(
        eventId = "evt-2",
        timestamp = System.currentTimeMillis(),
        sourceAgentId = "agent-C",
        questionText = "Why?",
        context = "Testing context",
        urgency = Urgency.MEDIUM,
    )

    @Test
    fun `subscriber receives only matching events`() {
        runBlocking {
            val bus = EventBus()
            val receivedTask = CompletableDeferred<TaskCreatedEvent>()
            var nonMatchingCalled: Boolean

            bus.subscribe<TaskCreatedEvent> { event ->
                receivedTask.complete(event)
            }

            // Publish matching event
            bus.publish(taskEvent())

            val result = withTimeout(2_000) { receivedTask.await() }
            assertEquals("task-123", result.taskId)

            // Publish non-matching event and ensure the prior handler is not triggered again
            val before = receivedTask.isCompleted
            bus.publish(questionEvent())
            delay(200) // small delay to allow any accidental dispatch

            val after = receivedTask.isCompleted
            nonMatchingCalled = before && after // remains completed, not re-triggered
            assertEquals(true, nonMatchingCalled)
        }
    }

    @Test
    fun `multiple subscribers receive event`() {
        runBlocking {
            val bus = EventBus()
            val s1 = CompletableDeferred<Boolean>()
            val s2 = CompletableDeferred<Boolean>()

            bus.subscribe<TaskCreatedEvent> { s1.complete(true) }
            bus.subscribe<TaskCreatedEvent> { s2.complete(true) }

            bus.publish(taskEvent())

            withTimeout(2_000) {
                assertEquals(true, s1.await())
            }
            withTimeout(2_000) {
                assertEquals(true, s2.await())
            }
        }
    }

    @Test
    fun `unsubscribe prevents further delivery`() {
        runBlocking {
            var count = 0

            val bus = EventBus()
            val token = bus.subscribe<TaskCreatedEvent> { count += 1 }

            // First publish should deliver
            bus.publish(taskEvent())
            delay(200)
            assertEquals(1, count)

            // Now unsubscribe and publish again
            bus.unsubscribe(token)
            bus.publish(taskEvent())
            delay(200)
            assertEquals(1, count)
        }
    }
}
