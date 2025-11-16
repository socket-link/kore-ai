package link.socket.kore.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import link.socket.kore.agents.events.Database
import link.socket.kore.agents.events.Event
import link.socket.kore.agents.events.EventId
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
        val payload: String = json.encodeToString(
            serializer = Event.serializer(),
            value = event,
        )

        queries.insertEvent(
            event_id = event.eventId,
            event_type = event.eventType,
            source_agent_id = event.sourceAgentId,
            timestamp = event.timestamp,
            payload = payload
        )
    }

    /**
     * Retrieve all events in reverse chronological order (newest first).
     */
    fun getAllEvents(): List<Event> =
        queries
            .getAllEvents()
            .executeAsList()
            .map { row ->
                deserialize(row.payload)
            }

    /**
     * Retrieve all events since the given epoch millis [timestamp], ascending by time.
     */
    fun getEventsSince(timestamp: Long): List<Event> =
        queries
            .getEventsSince(timestamp)
            .executeAsList()
            .map { row ->
                deserialize(row.payload)
            }

    /**
     * Retrieve all events filtered by [eventType] (e.g., "TaskCreatedEvent"), newest first.
     */
    fun getEventsByType(eventType: String): List<Event> =
        queries
            .getEventsByType(eventType)
            .executeAsList()
            .map { row ->
                deserialize(row.payload)
            }

    /**
     * Retrieve an event by its [eventId], or null if not present.
     */
    fun getEventById(eventId: EventId): Event? =
        queries
            .getEventById(eventId)
            .executeAsOneOrNull()
            ?.let { row ->
                deserialize(row.payload)
            }

    private fun deserialize(payload: String): Event =
        json.decodeFromString(
            deserializer = Event.serializer(),
            string = payload,
        )
}
