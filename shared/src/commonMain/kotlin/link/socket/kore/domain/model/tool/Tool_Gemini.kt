package link.socket.kore.domain.model.tool

sealed class Tool_Gemini(
    override val type: String,
    override val name: String,
) : ToolDefinition.Gemini(type, name) {

    data object CodeExecution : Tool_Gemini(
        type = "",
        name = "",
    )

    data object UrlContext : Tool_Gemini(
        type = "",
        name = "",
    )

    data object WebSearch : Tool_Gemini(
        type = "",
        name = "",
    )
}
