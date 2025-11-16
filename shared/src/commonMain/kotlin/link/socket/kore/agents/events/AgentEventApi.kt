package link.socket.kore.agents.events

/**
 * Expect declaration for generating globally-unique event IDs per platform.
 */
expect fun generateEventId(): String

/** Current epoch milliseconds, platform-specific. */
expect fun currentTimeMillis(): Long

/**
 * High-level, agent-friendly API for interacting with the EventBus.
 *
 * This facade hides event creation details and provides convenience publish/subscribe
 * methods that agents can call directly.
 */
class AgentEventApi(
    private val eventBus: EventBus,
    private val agentId: String,
) {
    /** Publish a TaskCreated event with auto-generated ID and current timestamp. */
    suspend fun publishTaskCreated(
        taskId: String,
        description: String,
        assignedTo: String? = null,
    ) {
        val event = TaskCreatedEvent(
            eventId = generateEventId(),
            timestamp = currentTimeMillis(),
            sourceAgentId = agentId,
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
        val event = QuestionRaisedEvent(
            eventId = generateEventId(),
            timestamp = currentTimeMillis(),
            sourceAgentId = agentId,
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
        val event = CodeSubmittedEvent(
            eventId = generateEventId(),
            timestamp = currentTimeMillis(),
            sourceAgentId = agentId,
            filePath = filePath,
            changeDescription = changeDescription,
            reviewRequired = reviewRequired,
        )
        eventBus.publish(event)
    }

    /** Subscribe to TaskCreated events. */
    fun onTaskCreated(handler: suspend (TaskCreatedEvent) -> Unit): SubscriptionToken =
        eventBus.subscribe(TaskCreatedEvent::class, handler)

    /** Subscribe to QuestionRaised events. */
    fun onQuestionRaised(handler: suspend (QuestionRaisedEvent) -> Unit): SubscriptionToken =
        eventBus.subscribe(QuestionRaisedEvent::class, handler)

    /** Subscribe to CodeSubmitted events. */
    fun onCodeSubmitted(handler: suspend (CodeSubmittedEvent) -> Unit): SubscriptionToken =
        eventBus.subscribe(CodeSubmittedEvent::class, handler)

    /** Retrieve all events since the provided epoch millis. */
    fun getRecentEvents(since: Long): List<Event> =
        eventBus.getEventHistory(since = since)
}
