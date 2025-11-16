package link.socket.kore.agents.events

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import link.socket.kore.data.DEFAULT_JSON
import link.socket.kore.data.EventRepository

@OptIn(ExperimentalCoroutinesApi::class)
class AgentEventBusIntegrationTest {

    private val json = DEFAULT_JSON
    private val scope = TestScope(UnconfinedTestDispatcher())
    private val eventBusFactory = EventBusFactory(scope)

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var eventRepository: EventRepository

    @BeforeTest
    fun setUp() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        eventRepository = EventRepository(json, scope, Database(driver))
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun `complete agent communication flow`() {
        runBlocking {
            val agentEventApiFactory = AgentEventApiFactory(eventRepository, eventBusFactory)

            // Setup: Three agents with one shared event bus
            val api1 = agentEventApiFactory.create("code-writer")
            val api2 = agentEventApiFactory.create("code-reviewer")
            val api3 = agentEventApiFactory.create("task-manager")

            // Code writer subscribes to tasks before any are published
            val received = CompletableDeferred<Event.TaskCreated>()
            api1.onTaskCreated { received.complete(it) }

            // Scenario: Task manager creates task (after subscription is in place)
            api3.publishTaskCreated(
                taskId = "TASK-001",
                description = "Implement event bus",
                assignedTo = "code-writer"
            )

            val event = received.await()
            assertEquals("code-writer", event.assignedTo)
            assertEquals("TASK-001", event.taskId)
            assertEquals(true, event.eventId.isNotBlank())

            // Code reviewer subscribes before code is submitted
            val codeSubmissions = mutableListOf<Event.CodeSubmitted>()
            api2.onCodeSubmitted { codeSubmissions.add(it) }

            // Code writer submits code (after reviewer subscription)
            api1.publishCodeSubmitted(
                filePath = "EventBus.kt",
                changeDescription = "Initial implementation",
                reviewRequired = true
            )

            delay(100)
            assertEquals(1, codeSubmissions.size)

            // Reviewer raises question
            api2.publishQuestionRaised(
                questionText = "Should we add error handling?",
                context = "Reviewing EventBus.kt",
                urgency = Urgency.HIGH
            )

            // Verify all events are in history
            val allEvents = api1.getRecentEvents(since = null)
            assertEquals(3, allEvents.size)
            assertEquals(true, allEvents.any { it is Event.TaskCreated })
            assertEquals(true, allEvents.any { it is Event.CodeSubmitted })
            assertEquals(true, allEvents.any { it is Event.QuestionRaised })
        }
    }
}
