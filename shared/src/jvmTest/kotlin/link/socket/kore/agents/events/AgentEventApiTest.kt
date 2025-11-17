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
import kotlinx.datetime.Clock
import link.socket.kore.data.DEFAULT_JSON
import link.socket.kore.data.EventRepository

@OptIn(ExperimentalCoroutinesApi::class)
class AgentEventApiTest {

    private val stubAgentId = "agent-A"
    private val stubAgentId2 = "agent-B"
    private val json = DEFAULT_JSON
    private val scope = TestScope(UnconfinedTestDispatcher())
    private val eventBusFactory = EventBusFactory(scope)

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var eventRepository: EventRepository
    private lateinit var eventBus: EventBus
    private lateinit var agentEventApiFactory: AgentEventApiFactory

    @BeforeTest
    fun setUp() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        val database = Database(driver)

        eventRepository = EventRepository(json, scope, database)
        eventBus = eventBusFactory.create(eventRepository)
        agentEventApiFactory = AgentEventApiFactory(eventBus)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun `agent can publish and subscribe to TaskCreated`() {
        runBlocking {
            val api = agentEventApiFactory.create(stubAgentId)

            val received = CompletableDeferred<Event.TaskCreated>()
            api.onTaskCreated { received.complete(it) }
            api.publishTaskCreated(
                taskId = "task-123",
                description = "Implement feature X",
            )

            val event = received.await()
            assertEquals(stubAgentId, event.eventSource.getIdentifier())
            assertEquals("task-123", event.taskId)
            assertEquals(true, event.eventId.isNotBlank())
        }
    }

    @Test
    fun `multiple subscribers receive same event`() {
        runBlocking {
            val api = agentEventApiFactory.create(stubAgentId)

            var c1 = 0
            var c2 = 0

            api.onTaskCreated { c1++ }
            api.onTaskCreated { c2++ }
            api.publishTaskCreated(
                taskId = "t1",
                description = "desc",
            )

            delay(200)
            assertEquals(1, c1)
            assertEquals(1, c2)
        }
    }

    @Test
    fun `events persist and can be queried historically`() {
        runBlocking {
            val api = agentEventApiFactory.create(stubAgentId)

            val since = Clock.System.now()
            delay(5)
            api.publishQuestionRaised(
                questionText = "Why?",
                context = "test",
                urgency = Urgency.HIGH,
            )

            // allow async persist
            delay(100)

            val events = api.getRecentEvents(since)
            assertEquals(true, events.any { it is Event.QuestionRaised })
        }
    }

    @Test
    fun `multiple AgentEventApi instances can coexist and observe their own agentId's events`() {
        runBlocking {
            val api1 = agentEventApiFactory.create(stubAgentId)
            val receivedA = CompletableDeferred<Event.TaskCreated>()
            api1.onTaskCreated(
                api1.eventCreatedByMeFilter,
            ) { e ->
                receivedA.complete(e)
            }

            val api2 = agentEventApiFactory.create(stubAgentId2)
            val receivedB = CompletableDeferred<Event.TaskCreated>()
            api2.onTaskCreated(
                api2.eventCreatedByMeFilter,
            ) { e ->
                receivedB.complete(e)
            }

            api1.publishTaskCreated("tA", "from A")
            api2.publishTaskCreated("tB", "from B")

            val eA = receivedA.await()
            val eB = receivedB.await()
            assertEquals(stubAgentId, eA.eventSource.getIdentifier())
            assertEquals(stubAgentId2, eB.eventSource.getIdentifier())
            assertNotEquals(eA.eventId, eB.eventId)
        }
    }

    @Test
    fun `code submitted event can be published and subscribed`() {
        runBlocking {
            val api = agentEventApiFactory.create(stubAgentId)
            val received = CompletableDeferred<Event.CodeSubmitted>()

            api.onCodeSubmitted { received.complete(it) }
            api.publishCodeSubmitted(
                filePath = "/tmp/a.kt",
                changeDescription = "Add feature",
                reviewRequired = true,
            )

            val e = received.await()
            assertIs<Event.CodeSubmitted>(e)
            assertEquals("/tmp/a.kt", e.filePath)
            assertEquals(true, e.reviewRequired)
        }
    }
}
