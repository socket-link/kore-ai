package link.socket.kore.domain.model.tool

sealed class ToolDefinition(
    open val type: String,
    open val name: String,
)
