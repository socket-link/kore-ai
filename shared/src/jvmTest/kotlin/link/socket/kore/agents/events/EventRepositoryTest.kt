package link.socket.kore.agents.events

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlin.io.path.createTempDirectory
import kotlin.io.path.div
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import link.socket.kore.data.DEFAULT_JSON
import link.socket.kore.data.EventRepository

class EventRepositoryTest {

    private val scope = TestScope()
    private val stubJson = DEFAULT_JSON
    private lateinit var driver: JdbcSqliteDriver

    @BeforeTest
    fun setUp() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    private fun repo(): EventRepository =
        EventRepository(stubJson, scope, Database(driver))

    private fun sampleTask(
        id: String = "evt-task-1",
        ts: Long = System.currentTimeMillis(),
    ) = TaskCreatedEvent(
        eventId = id,
        timestamp = ts,
        sourceAgentId = "agent-A",
        taskId = "task-123",
        description = "Test Task",
        assignedTo = "agent-B"
    )

    private fun sampleQuestion(
        id: String = "evt-q-1",
        ts: Long = System.currentTimeMillis(),
    ) = QuestionRaisedEvent(
        eventId = id,
        timestamp = ts,
        sourceAgentId = "agent-C",
        questionText = "Why?",
        context = "UnitTest",
        urgency = Urgency.MEDIUM
    )

    @Test
    fun `save and retrieve by id`() {
        runBlocking {
            val repo = repo()
            val event = sampleTask(id = "evt-100", ts = 1000)
            repo.saveEvent(event)

            val loaded = repo.getEventById("evt-100")
            assertNotNull(loaded)
            assertIs<TaskCreatedEvent>(loaded)
            assertEquals("task-123", loaded.taskId)
        }
    }

    @Test
    fun `query by type returns only matching`() {
        runBlocking {
            val repo = repo()
            val t1 = sampleTask(id = "evt-1", ts = 1_000)
            val q1 = sampleQuestion(id = "evt-2", ts = 2_000)
            repo.saveEvent(t1)
            repo.saveEvent(q1)

            val tasks = repo.getEventsByType("TaskCreatedEvent")
            assertEquals(1, tasks.size)
            assertIs<TaskCreatedEvent>(tasks.first())
        }
    }

    @Test
    fun querySinceReturnsAscending() {
        runBlocking {
            val repo = repo()
            val t1 = sampleTask(id = "evt-1", ts = 1_000)
            val t2 = sampleTask(id = "evt-2", ts = 3_000)
            val q1 = sampleQuestion(id = "evt-3", ts = 2_000)
            repo.saveEvent(t1)
            repo.saveEvent(t2)
            repo.saveEvent(q1)

            val since = repo.getEventsSince(1_500)
            assertEquals(listOf("evt-3", "evt-2"), since.map { it.eventId })
        }
    }

    @Test
    fun `events persist across restart`() {
        runBlocking {
            val dir = createTempDirectory(prefix = "events-db")
            val dbFile = dir / "events.sqlite"

            // First open
            val driver1 = JdbcSqliteDriver("jdbc:sqlite:$dbFile")
            Database.Schema.create(driver1)

            val repo1 = EventRepository(stubJson, scope, Database(driver1))
            val e = sampleTask(id = "evt-persist", ts = 42_000)
            repo1.saveEvent(e)
            driver1.close()

            // Re-open same file
            val driver2 = JdbcSqliteDriver("jdbc:sqlite:$dbFile")
            val repo2 = EventRepository(stubJson, scope, Database(driver2))
            val loaded = repo2.getEventById("evt-persist")

            assertNotNull(loaded)
            assertIs<TaskCreatedEvent>(loaded)
            driver2.close()
        }
    }
}
