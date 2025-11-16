package link.socket.kore.agents.events

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.serialization.json.Json

class EventSerializationTest {

    private val json = Json {
        prettyPrint = false
        encodeDefaults = true
        classDiscriminator = "type"
    }

    @Test
    fun `serialize and deserialize task created event as concrete`() {
        val original = TaskCreatedEvent(
            eventId = "11111111-1111-1111-1111-111111111111",
            timestamp = 1731690720000,
            sourceAgentId = "agent-A",
            taskId = "T-001",
            description = "Implement event bus",
            assignedTo = "agent-B",
        )
        val text = json.encodeToString(original)
        val decoded = json.decodeFromString<TaskCreatedEvent>(text)
        assertEquals(original, decoded)
    }

    @Test
    fun `serialize and deserialize question raised event polymorphic`() {
        val original: Event = QuestionRaisedEvent(
            eventId = "22222222-2222-2222-2222-222222222222",
            timestamp = 1731690721000,
            sourceAgentId = "agent-B",
            questionText = "What database should we use?",
            context = "KMP persistence options",
            urgency = Urgency.HIGH,
        )
        val text = json.encodeToString(Event.serializer(), original)
        val decoded = json.decodeFromString(Event.serializer(), text)
        assertIs<QuestionRaisedEvent>(decoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `serialize and deserialize code submitted event as concrete`() {
        val original = CodeSubmittedEvent(
            eventId = "33333333-3333-3333-3333-333333333333",
            timestamp = 1731690722000,
            sourceAgentId = "agent-C",
            filePath = "shared/src/commonMain/kotlin/EventBus.kt",
            changeDescription = "Initial commit",
            reviewRequired = true,
        )
        val text = json.encodeToString(original)
        val decoded = json.decodeFromString<CodeSubmittedEvent>(text)
        assertEquals(original, decoded)
    }
}
