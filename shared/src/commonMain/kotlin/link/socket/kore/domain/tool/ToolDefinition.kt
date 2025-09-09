package link.socket.kore.domain.tool

sealed class ToolDefinition(
    open val type: String,
    open val name: String,
)
