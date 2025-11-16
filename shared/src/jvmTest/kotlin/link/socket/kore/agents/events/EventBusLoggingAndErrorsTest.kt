package link.socket.kore.agents.events

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.datetime.Clock
import link.socket.kore.data.DEFAULT_JSON
import link.socket.kore.data.EventRepository

@OptIn(ExperimentalCoroutinesApi::class)
class EventBusLoggingAndErrorsTest {

    private val scope = TestScope(UnconfinedTestDispatcher())
    private val json = DEFAULT_JSON

    private val stubTimestamp = Clock.System.now()
    private val stubEventSource = EventSource.Agent("agent-X")

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var db: Database

    @BeforeTest
    fun setUp() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        db = Database(driver)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    private fun taskEvent(): Event.TaskCreated = Event.TaskCreated(
        eventId = "evt-log-1",
        timestamp = stubTimestamp,
        eventSource = stubEventSource,
        taskId = "task-1",
        description = "desc",
        assignedTo = null,
    )

    private class TestLogger : EventLogger {
        val publishes = mutableListOf<String>()
        val subscriptions = mutableListOf<Pair<String, String>>()
        val errors = mutableListOf<String>()

        override fun logPublish(event: Event) {
            publishes += event.eventId
        }

        override fun logSubscription(eventType: String, subscriberId: String) {
            subscriptions += eventType to subscriberId
        }

        override fun logError(message: String, throwable: Throwable?) {
            errors += message
        }
    }

    @Test
    fun `subscriber exceptions do not affect others and are logged`() {
        runBlocking {
            val logger = TestLogger()
            val repo = EventRepository(json, scope, db)
            val bus = EventBus(scope, repo, logger)

            var goodCalled = false
            bus.subscribe<Event.TaskCreated> { throw IllegalStateException("boom") }
            bus.subscribe<Event.TaskCreated> { goodCalled = true }

            bus.publish(taskEvent())
            delay(200)

            assertEquals(true, goodCalled)
            assertEquals(true, logger.publishes.contains("evt-log-1"))
            assertEquals(true, logger.errors.any { it.contains("Subscriber handler failure") })
        }
    }

    @Test
    fun `database write failures are logged but do not crash`() {
        runBlocking {
            val logger = TestLogger()
            val repo = EventRepository(json, scope, db)
            val bus = EventBus(scope, repo, logger)

            var delivered = false
            bus.subscribe<Event.TaskCreated> { delivered = true }

            // Simulate failure by corrupting table (drop table name typo) is heavy; instead call publish and ensure regardless of save failure subscribers receive.
            // We cannot inject proxy easily without changing production code further; use SQL constraint failure: insert duplicate primary key to trigger persistence error.
            val e = taskEvent()

            // First publish succeeds and inserts
            bus.publish(e)
            delay(100)

            // Publish same event again -> primary key conflict should cause EventPersistenceException which is logged
            bus.publish(e)
            delay(200)

            assertEquals(true, delivered)
            assertEquals(true, logger.errors.any { it.contains("Failed to persist event") })
        }
    }

    @Test
    fun `malformed JSON in database is handled gracefully`() {
        runBlocking {
            val logger = TestLogger()
            val repo = EventRepository(json, scope, db)
            val bus = EventBus(scope, repo, logger)

            // Insert a malformed row directly
            db.eventStoreQueries.insertEvent(
                event_id = "bad-json-1",
                event_type = "TaskCreatedEvent",
                source_id = "agent-X",
                timestamp = 1L,
                payload = "{ this is not valid json }",
            )

            // Should not crash; history should be empty because decoding failed
            val history = bus.getEventHistory()
            assertEquals(true, history.isEmpty())
            assertEquals(true, logger.errors.any { error ->
                error.contains("Failed to load event history")
            })
        }
    }
}
