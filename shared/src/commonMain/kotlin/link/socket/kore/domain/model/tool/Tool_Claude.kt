@file:Suppress("ClassName")

package link.socket.kore.domain.model.tool

sealed class Tool_Claude(
    override val type: String,
    override val name: String,
) : ToolDefinition(type, name) {

    data object Bash : Tool_Claude(
        type = "bash_20250124",
        name = "bash",
    )

    /**
     * Currently in beta, so it also requires this header:
     * "anthropic-beta": "code-execution-2025-05-22"
     */
    data object CodeExecution : Tool_Claude(
        type = "code_execution_20250522",
        name = "code_execution",
    )

    sealed class TextEditor(
        override val name: String,
        override val type: String,
    ) : Tool_Claude(name, type) {

        data object _4 : TextEditor(
            type = "text_editor_20250728",
            name = "str_replace_based_edit_tool",
        )

        data object _3_7 : TextEditor(
            type = "text_editor_20250124",
            name = "str_replace_based_edit_tool",
        )
    }

    data object WebSearch : Tool_Claude(
        type = "web_search_20250305",
        name = "web_search",
    )
}
