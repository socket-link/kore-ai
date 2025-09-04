@file:Suppress("ClassName")

package link.socket.kore.domain.model.tool

sealed class Tool_OpenAI(
    override val type: String,
    override val name: String,
) : ToolDefinition(type, name) {

    data object CodeExecution : Tool_OpenAI(
        type = "",
        name = "",
    )

    data object FileSearch : Tool_OpenAI(
        type = "",
        name = "",
    )

    data object ImageGeneration : Tool_OpenAI(
        type = "",
        name = "",
    )

    data object MCP : Tool_OpenAI(
        type = "",
        name = "",
    )

    data object WebSearch : Tool_OpenAI(
        type = "",
        name = "",
    )
}
