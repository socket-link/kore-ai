package link.socket.kore.agents.events

import java.util.UUID

actual fun generateEventId(): String =
    UUID.randomUUID().toString()
