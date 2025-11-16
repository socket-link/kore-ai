package link.socket.kore.agents.events

/**
 * Lightweight logger interface for EventBus diagnostics.
 */
interface EventLogger {
    /** Called whenever an event is published. */
    fun logPublish(event: Event)

    /** Called when a subscription (or unsubscription) occurs. */
    fun logSubscription(eventType: String, subscriberId: String)

    /** Log an error without crashing the app. */
    fun logError(message: String, throwable: Throwable? = null)
}

/**
 * Basic console logger that prints structured messages.
 */
class ConsoleEventLogger : EventLogger {
    override fun logPublish(event: Event) {
        println("[EventBus][PUBLISH] type=${event.eventType} id=${event.eventId} ts=${event.timestamp} src=${event.eventSource.getIdentifier()}")
    }

    override fun logSubscription(eventType: String, subscriberId: String) {
        println("[EventBus][SUBSCRIPTION] type=$eventType subscriber=$subscriberId")
    }

    override fun logError(message: String, throwable: Throwable?) {
        println("[EventBus][ERROR] $message" + (throwable?.let { ": ${it::class.simpleName} - ${it.message}" } ?: ""))
        throwable?.printStackTrace()
    }
}
