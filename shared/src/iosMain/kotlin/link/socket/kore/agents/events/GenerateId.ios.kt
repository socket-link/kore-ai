package link.socket.kore.agents.events

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSUUID
import platform.posix.time

actual fun generateEventId(): String =
    NSUUID().UUIDString()

@OptIn(ExperimentalForeignApi::class)
actual fun currentTimeMillis(): Long =
    time(null) * 1000L
