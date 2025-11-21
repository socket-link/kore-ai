package link.socket.kore.domain.tool

import kotlinx.serialization.Serializable

@Serializable
sealed class AITool(
    open val type: String,
    open val name: String,
)
