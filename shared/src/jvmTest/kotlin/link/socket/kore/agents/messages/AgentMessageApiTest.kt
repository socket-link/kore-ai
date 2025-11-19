package link.socket.kore.agents.messages

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import link.socket.kore.agents.events.Database
import link.socket.kore.agents.events.EventBus
import link.socket.kore.agents.events.EventBusFactory
import link.socket.kore.agents.events.EventStatus
import link.socket.kore.agents.events.MessageEvent
import link.socket.kore.agents.events.messages.AgentMessageApiFactory
import link.socket.kore.agents.events.messages.MessageChannel
import link.socket.kore.data.DEFAULT_JSON
import link.socket.kore.data.EventRepository
import link.socket.kore.data.MessageRepository

@OptIn(ExperimentalCoroutinesApi::class)
class AgentMessageApiTest {

    private val stubAgentId = "agent-A"
    private val stubAgentId2 = "agent-B"
    private val json = DEFAULT_JSON
    private val scope = TestScope(UnconfinedTestDispatcher())
    private val eventBusFactory = EventBusFactory(scope)

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var eventRepository: EventRepository
    private lateinit var messageRepository: MessageRepository
    private lateinit var eventBus: EventBus
    private lateinit var agentMessageApiFactory: AgentMessageApiFactory

    @BeforeTest
    fun setup() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        val database = Database(driver)

        eventRepository = EventRepository(json, scope, database)
        messageRepository = MessageRepository(json, scope, database)
        eventBus = eventBusFactory.create()
        agentMessageApiFactory = AgentMessageApiFactory(messageRepository, eventBus)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun `create thread, escalate status, then resolve`() {
        runBlocking {
            val api = agentMessageApiFactory.create(stubAgentId)
            val received = mutableListOf<MessageEvent>()

            val threadCreatedSubscription = api.onThreadCreated { event, _ ->
                received += event
            }

            val escalationRequestedSubscription = api.onEscalationRequested { event, _ ->
                received += event
            }

            val threadStatusChangedSubscription = api.onThreadStatusChanged { event, _ ->
                received += event
            }

            // Subscribe to channel message posts to capture initial and subsequent messages
            val channelMessagePostedSubscription = api.onChannelMessagePosted(
                channel = MessageChannel.Public.Engineering,
            ) { event, _ ->
                received += event
            }

            // Create
            val thread = api.createThread(
                participants = setOf(stubAgentId2),
                channel = MessageChannel.Public.Engineering,
                initialMessageContent = "Kickoff",
            )

            val fetchedThread1 = api.getThread(thread.id).getOrNull()
            assertNotNull(fetchedThread1)
            assertEquals(EventStatus.OPEN, fetchedThread1.status)
            assertEquals(2, fetchedThread1.participants.size) // sender + agent-B
            assertEquals(1, fetchedThread1.messages.size)

            // Post
            val message = api.postMessage(
                threadId = thread.id,
                content = "Update",
            )
            assertEquals("Update", message.content)

            val fetchedThread2 = api.getThread(thread.id).getOrNull()
            assertNotNull(fetchedThread2)
            assertEquals(2, fetchedThread2.messages.size)

            // Escalate -> WAITING_FOR_HUMAN
            api.escalateToHuman(
                threadId = thread.id,
                reason = "Need approval",
            )
            val fetchedThread3 = api.getThread(thread.id).getOrNull()
            assertNotNull(fetchedThread3)
            assertEquals(EventStatus.WAITING_FOR_HUMAN, fetchedThread3.status)

            // Posting now should fail, since the thread is waiting for human
            var threw = false
            try {
                api.postMessage(
                    threadId = thread.id,
                    content = "Should fail",
                )
            } catch (e: IllegalArgumentException) {
                threw = true
            } catch (e: IllegalStateException) {
                threw = true
            }
            assertTrue(threw)

            // Resolve
            api.resolveThread(thread.id)
            val fetched4 = api.getThread(thread.id).getOrNull()
            assertNotNull(fetched4)
            assertEquals(EventStatus.RESOLVED, fetched4.status)

            // allow async event handlers to run
            delay(200)

            // Events were published (at least 1 create, 2 posts, 1 escalation, 2 status changes)
            assertTrue(received.any { it is MessageEvent.ThreadCreated })
            assertTrue(received.count { it is MessageEvent.MessagePosted } >= 2)
            assertTrue(received.any { it is MessageEvent.EscalationRequested })
            assertTrue(received.count { it is MessageEvent.ThreadStatusChanged } >= 2)

            //** TODO: Test subscriptions can be unsubscribed from. */
        }
    }
}
