package link.socket.kore.agents.events

import platform.Foundation.NSUUID

actual fun generateEventId(): String =
    NSUUID().UUIDString()
