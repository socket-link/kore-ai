package link.socket.kore.agents.events

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import link.socket.kore.agents.core.AgentId

/**
 * Expect declaration for generating globally-unique event IDs per platform.
 */
expect fun generateEventId(): String

/**
 * High-level, agent-friendly API for interacting with the EventBus.
 *
 * This facade hides event creation details and provides convenience publish/subscribe
 * methods that agents can call directly.
 */
class AgentEventApi(
    private val eventBus: EventBus,
    private val agentId: AgentId,
) {
    /** Publish a TaskCreated event with auto-generated ID and current timestamp. */
    suspend fun publishTaskCreated(
        taskId: String,
        description: String,
        assignedTo: AgentId? = null,
    ) {
        val event = Event.TaskCreated(
            eventId = generateEventId(),
            timestamp = Clock.System.now(),
            eventSource = EventSource.Agent(agentId),
            taskId = taskId,
            description = description,
            assignedTo = assignedTo,
        )
        eventBus.publish(event)
    }

    /** Publish a QuestionRaised event with auto-generated ID and current timestamp. */
    suspend fun publishQuestionRaised(
        questionText: String,
        context: String,
        urgency: Urgency = Urgency.MEDIUM,
    ) {
        val event = Event.QuestionRaised(
            eventId = generateEventId(),
            timestamp = Clock.System.now(),
            eventSource = EventSource.Agent(agentId),
            questionText = questionText,
            context = context,
            urgency = urgency,
        )
        eventBus.publish(event)
    }

    /** Publish a CodeSubmitted event with auto-generated ID and current timestamp. */
    suspend fun publishCodeSubmitted(
        filePath: String,
        changeDescription: String,
        reviewRequired: Boolean = false,
    ) {
        val event = Event.CodeSubmitted(
            eventId = generateEventId(),
            timestamp = Clock.System.now(),
            eventSource = EventSource.Agent(agentId),
            filePath = filePath,
            changeDescription = changeDescription,
            reviewRequired = reviewRequired,
        )
        eventBus.publish(event)
    }

    /** Subscribe to TaskCreated events. */
    fun onTaskCreated(handler: suspend (Event.TaskCreated) -> Unit): SubscriptionToken =
        eventBus.subscribe(Event.TaskCreated::class, handler)

    /** Subscribe to QuestionRaised events. */
    fun onQuestionRaised(handler: suspend (Event.QuestionRaised) -> Unit): SubscriptionToken =
        eventBus.subscribe(Event.QuestionRaised::class, handler)

    /** Subscribe to CodeSubmitted events. */
    fun onCodeSubmitted(handler: suspend (Event.CodeSubmitted) -> Unit): SubscriptionToken =
        eventBus.subscribe(Event.CodeSubmitted::class, handler)

    /** Retrieve all events since the provided epoch millis. */
    fun getRecentEvents(since: Instant?): List<Event> =
        eventBus.getEventHistory(since = since)
}
