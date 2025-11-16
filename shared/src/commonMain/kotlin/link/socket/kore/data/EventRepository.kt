package link.socket.kore.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import link.socket.kore.agents.events.Database
import link.socket.kore.agents.events.Event
import link.socket.kore.agents.events.EventId
import link.socket.kore.agents.events.EventPersistenceException
import link.socket.kore.agents.events.EventSerializationException
import link.socket.kore.agents.events.EventStoreQueries

/**
 * Repository responsible for persisting and querying Events using SQLDelight.
 *
 * This lives in common code and works across KMP targets. Callers are responsible for
 * providing a platform-specific SQLDelight [SqlDriver] to construct the generated [link.socket.kore.agents.events.Database]
 * instance and then pass it into this repository.
 */
class EventRepository(
    override val json: Json,
    override val scope: CoroutineScope,
    private val database: Database,
) : Repository<EventId, Event>(json, scope) {

    override val tag: String = "Event${super.tag}"

    private val queries: EventStoreQueries
        get() = database.eventStoreQueries

    /**
     * Persist the given [event] by serializing it to JSON and inserting into the event_store table.
     */
    fun saveEvent(event: Event) {
        saveEventResult(event).getOrThrow()
    }

    /** Safe variant that wraps errors in [Result]. */
    fun saveEventResult(event: Event): Result<Unit> = runCatching {
        val payload: String = encode(event)
        runCatching {
            queries.insertEvent(
                event_id = event.eventId,
                event_type = event.eventType,
                source_agent_id = event.sourceAgentId,
                timestamp = event.timestamp,
                payload = payload
            )
        }.getOrElse { t ->
            throw EventPersistenceException("Failed to insert event ${event.eventId}", t)
        }
    }

    /**
     * Retrieve all events in reverse chronological order (newest first).
     */
    fun getAllEvents(): List<Event> = getAllEventsResult().getOrThrow()

    fun getAllEventsResult(): Result<List<Event>> = runCatching {
        val rows = runCatching { queries.getAllEvents().executeAsList() }
            .getOrElse { t -> throw EventPersistenceException("Failed to query all events", t) }
        rows.map { row -> decode(row.payload) }
    }

    /**
     * Retrieve all events since the given epoch millis [timestamp], ascending by time.
     */
    fun getEventsSince(timestamp: Long): List<Event> = getEventsSinceResult(timestamp).getOrThrow()

    fun getEventsSinceResult(timestamp: Long): Result<List<Event>> = runCatching {
        val rows = runCatching { queries.getEventsSince(timestamp).executeAsList() }
            .getOrElse { t -> throw EventPersistenceException("Failed to query events since $timestamp", t) }
        rows.map { row -> decode(row.payload) }
    }

    /**
     * Retrieve all events filtered by [eventType] (e.g., "TaskCreatedEvent"), newest first.
     */
    fun getEventsByType(eventType: String): List<Event> = getEventsByTypeResult(eventType).getOrThrow()

    fun getEventsByTypeResult(eventType: String): Result<List<Event>> = runCatching {
        val rows = runCatching { queries.getEventsByType(eventType).executeAsList() }
            .getOrElse { t -> throw EventPersistenceException("Failed to query events by type $eventType", t) }
        rows.map { row -> decode(row.payload) }
    }

    /**
     * Retrieve an event by its [eventId], or null if not present.
     */
    fun getEventById(eventId: EventId): Event? = getEventByIdResult(eventId).getOrThrow()

    fun getEventByIdResult(eventId: EventId): Result<Event?> = runCatching {
        val row = runCatching { queries.getEventById(eventId).executeAsOneOrNull() }
            .getOrElse { t -> throw EventPersistenceException("Failed to query event by id $eventId", t) }
        row?.let { decode(it.payload) }
    }

    private fun encode(event: Event): String = try {
        json.encodeToString(
            serializer = Event.serializer(),
            value = event,
        )
    } catch (se: SerializationException) {
        throw EventSerializationException("Failed to serialize event ${event.eventId}", se)
    } catch (t: Throwable) {
        throw EventSerializationException("Failed to serialize event ${event.eventId}", t)
    }

    private fun decode(payload: String): Event = try {
        json.decodeFromString(
            deserializer = Event.serializer(),
            string = payload,
        )
    } catch (se: SerializationException) {
        throw EventSerializationException("Failed to deserialize event payload", se)
    } catch (t: Throwable) {
        throw EventSerializationException("Failed to deserialize event payload", t)
    }
}
