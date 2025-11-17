package link.socket.kore.agents.events

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import link.socket.kore.agents.core.AgentId
import link.socket.kore.data.EventRepository

/**
 * Expect declaration for generating globally-unique event IDs per platform.
 */
expect fun generateEventId(): String

val AgentEventApi.eventCreatedByMeFilter
    get() = { event: Event -> event.eventSource.getIdentifier() == agentId }

val AgentEventApi.taskAssignedToMeFilter
    get() = { event: Event.TaskCreated -> event.assignedTo == agentId }

/**
 * High-level, agent-friendly API for interacting with the EventBus.
 *
 * This facade hides event creation details and provides convenience publish/subscribe
 * methods that agents can call directly.
 */
class AgentEventApi(
    val agentId: AgentId,
    private val eventRepository: EventRepository,
    private val eventBus: EventBus,
    private val logger: EventLogger = ConsoleEventLogger(),
) {

    /** Persist and publish a pre-constructed event. */
    suspend fun publish(event: Event) {
        eventRepository.saveEvent(event)
            .onSuccess {
                eventBus.publish(event)
            }
            .onFailure { throwable ->
                logger.logError(
                    message = "Failed to create event ${event.eventType} id=${event.eventId}",
                    throwable = throwable,
                )
            }
    }

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

        publish(event)
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

        publish(event)
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

        publish(event)
    }

    /** Subscribe to TaskCreated events. */
    fun onTaskCreated(
        filter: (Event.TaskCreated) -> Boolean = { true },
        handler: suspend (Event.TaskCreated) -> Unit,
    ): SubscriptionToken =
        eventBus.subscribe(
            eventClass = Event.TaskCreated::class,
        ) { event ->
            if (filter(event)) {
                handler(event)
            }
        }

    /** Subscribe to QuestionRaised events. */
    fun onQuestionRaised(
        filter: (Event.QuestionRaised) -> Boolean = { true },
        handler: suspend (Event.QuestionRaised) -> Unit,
    ): SubscriptionToken =
        eventBus.subscribe(
            eventClass = Event.QuestionRaised::class,
        ) { event ->
            if (filter(event)) {
                handler(event)
            }
        }

    /** Subscribe to CodeSubmitted events. */
    fun onCodeSubmitted(
        filter: (Event.CodeSubmitted) -> Boolean = { true },
        handler: suspend (Event.CodeSubmitted) -> Unit,
    ): SubscriptionToken =
        eventBus.subscribe(
            eventClass = Event.CodeSubmitted::class,
        ) { event ->
            if (filter(event)) {
                handler(event)
            }
        }

    /** Retrieve all events since the provided timestamp, or all if null. */
    suspend fun getRecentEvents(since: Instant?): List<Event> {
        val result = if (since != null) {
            eventRepository.getEventsSince(since)
        } else {
            eventRepository.getAllEvents()
        }

        return result.onFailure { th ->
            logger.logError(
                message = "Failed to load recent events since=$since",
                throwable = th,
            )
        }.getOrElse { emptyList() }
    }

    /** Retrieve historical events with optional type filter and since timestamp. */
    suspend fun getEventHistory(eventType: String? = null, since: Instant? = null): List<Event> {
        val result: Result<List<Event>> = when {
            eventType != null && since != null -> {
                eventRepository
                    .getEventsByType(eventType)
                    .map { list -> list.filter { it.timestamp >= since } }
            }
            eventType != null -> eventRepository.getEventsByType(eventType)
            since != null -> eventRepository.getEventsSince(since)
            else -> eventRepository.getAllEvents()
        }

        return result.onFailure { th ->
            logger.logError(
                message = "Failed to load event history (type=$eventType since=$since)",
                throwable = th,
            )
        }.getOrElse { emptyList() }
    }

    /** Replay past events by publishing them to current subscribers. */
    suspend fun replayEvents(since: Instant?) {
        val events = getRecentEvents(since)
        for (e in events) {
            eventBus.publish(e)
        }
    }
}
