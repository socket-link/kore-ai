package link.socket.kore.agents.events

/** Thrown when event JSON encoding/decoding fails. */
class EventSerializationException(message: String, cause: Throwable? = null) : Exception(message, cause)

/** Thrown when persisting or querying events from the database fails. */
class EventPersistenceException(message: String, cause: Throwable? = null) : Exception(message, cause)
