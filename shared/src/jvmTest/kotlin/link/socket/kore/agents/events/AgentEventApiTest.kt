package link.socket.kore.agents.events

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import link.socket.kore.data.DEFAULT_JSON
import link.socket.kore.data.EventRepository

@OptIn(ExperimentalCoroutinesApi::class)
class AgentEventApiTest {

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
    fun `agent can publish and subscribe to TaskCreated`() {
        runBlocking {
            val agentEventApiFactory = AgentEventApiFactory(eventRepository, eventBusFactory)
            val api = agentEventApiFactory.create("agent-1")

            val received = CompletableDeferred<TaskCreatedEvent>()
            api.onTaskCreated { received.complete(it) }
            api.publishTaskCreated(taskId = "task-123", description = "Implement feature X")

            val event = received.await()
            assertEquals("agent-1", event.sourceAgentId)
            assertEquals("task-123", event.taskId)
            assertEquals(true, event.eventId.isNotBlank())
        }
    }

    @Test
    fun `multiple subscribers receive same event`() {
        runBlocking {
            val agentEventApiFactory = AgentEventApiFactory(eventRepository, eventBusFactory)
            val api = agentEventApiFactory.create("agent-1")

            var c1 = 0
            var c2 = 0

            api.onTaskCreated { c1++ }
            api.onTaskCreated { c2++ }
            api.publishTaskCreated(taskId = "t1", description = "desc")

            delay(200)
            assertEquals(1, c1)
            assertEquals(1, c2)
        }
    }

    @Test
    fun `events persist and can be queried historically`() {
        runBlocking {
            val agentEventApiFactory = AgentEventApiFactory(eventRepository, eventBusFactory)
            val api = agentEventApiFactory.create("agent-1")

            val since = currentTimeMillis()
            delay(5)
            api.publishQuestionRaised(
                questionText = "Why?",
                context = "test",
                urgency = Urgency.HIGH,
            )

            // allow async persist
            delay(100)

            val events = api.getRecentEvents(since)
            assertEquals(true, events.any { it is QuestionRaisedEvent })
        }
    }

    @Test
    fun `multiple AgentEventApi instances can coexist and stamp agentId`() {
        runBlocking {
            val agentEventApiFactory = AgentEventApiFactory(eventRepository, eventBusFactory)
            val a1 = agentEventApiFactory.create("agent-A")
            val a2 = agentEventApiFactory.create("agent-B")

            val receivedA = CompletableDeferred<TaskCreatedEvent>()
            val receivedB = CompletableDeferred<TaskCreatedEvent>()
            a1.onTaskCreated { receivedA.complete(it) }
            a2.onTaskCreated { receivedB.complete(it) }

            a1.publishTaskCreated("tA", "from A")
            a2.publishTaskCreated("tB", "from B")

            val eA = receivedA.await()
            val eB = receivedB.await()
            assertEquals("agent-A", eA.sourceAgentId)
            assertEquals("agent-B", eB.sourceAgentId)
            assertNotEquals(eA.eventId, eB.eventId)
        }
    }

    @Test
    fun `code submitted event can be published and subscribed`() {
        runBlocking {
            val agentEventApiFactory = AgentEventApiFactory(eventRepository, eventBusFactory)
            val api = agentEventApiFactory.create("agent-1")
            val received = CompletableDeferred<CodeSubmittedEvent>()

            api.onCodeSubmitted { received.complete(it) }
            api.publishCodeSubmitted(filePath = "/tmp/a.kt", changeDescription = "Add feature", reviewRequired = true)

            val e = received.await()
            assertIs<CodeSubmittedEvent>(e)
            assertEquals("/tmp/a.kt", e.filePath)
            assertEquals(true, e.reviewRequired)
        }
    }
}
