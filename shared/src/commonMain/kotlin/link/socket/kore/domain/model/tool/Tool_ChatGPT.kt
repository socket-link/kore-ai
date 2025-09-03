@file:Suppress("ClassName")

package link.socket.kore.domain.model.tool

sealed class Tool_ChatGPT(
    override val type: String,
    override val name: String,
) : ToolDefinition(type, name) {

    data object CodeExecution : Tool_ChatGPT(
        type = "",
        name = "",
    )

    data object FileSearch : Tool_ChatGPT(
        type = "",
        name = "",
    )

    data object ImageGeneration : Tool_ChatGPT(
        type = "",
        name = "",
    )

    data object MCP : Tool_ChatGPT(
        type = "",
        name = "",
    )

    data object WebSearch : Tool_ChatGPT(
        type = "",
        name = "",
    )
}
