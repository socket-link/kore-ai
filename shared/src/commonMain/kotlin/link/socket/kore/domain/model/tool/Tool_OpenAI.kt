package link.socket.kore.domain.model.tool

sealed class Tool_OpenAI(
    override val type: String,
    override val name: String,
) : ToolDefinition.OpenAI(type, name) {

    data object CodeExecution : Tool_OpenAI(
        type = "",
        name = "",
    )

    data object UrlContext : Tool_OpenAI(
        type = "",
        name = "",
    )

    data object WebSearch : Tool_OpenAI(
        type = "",
        name = "",
    )
}
