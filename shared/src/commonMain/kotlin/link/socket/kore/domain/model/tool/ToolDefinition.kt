package link.socket.kore.domain.model.tool

sealed interface ToolDefinition {

    sealed class Claude(
        open val type: String,
        open val name: String,
    ) : ToolDefinition

    sealed class Gemini(
        open val type: String,
        open val name: String,
    ) : ToolDefinition

    sealed class OpenAI(
        open val type: String,
        open val name: String,
    ) : ToolDefinition
}
