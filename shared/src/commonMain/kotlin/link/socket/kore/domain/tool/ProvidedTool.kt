@file:Suppress("ClassName")

package link.socket.kore.domain.tool

sealed class ProvidedTool <T : ToolDefinition> (open val definition: T) {
    data class Bash <T : ToolDefinition> (override val definition: T) : ProvidedTool<T>(definition)
    data class CodeExecution <T : ToolDefinition> (override val definition: T) : ProvidedTool<T>(definition)
    data class ComputerUse <T : ToolDefinition> (override val definition: T) : ProvidedTool<T>(definition)
    data class Connector <T : ToolDefinition> (override val definition: T) : ProvidedTool<T>(definition)
    data class FileSearch <T : ToolDefinition> (override val definition: T) : ProvidedTool<T>(definition)
    data class ImageGeneration <T : ToolDefinition> (override val definition: T) : ProvidedTool<T>(definition)
    data class MCP <T : ToolDefinition> (override val definition: T) : ProvidedTool<T>(definition)
    data class TextEditor <T : ToolDefinition> (override val definition: T) : ProvidedTool<T>(definition)
    data class UrlContext <T : ToolDefinition> (override val definition: T) : ProvidedTool<T>(definition)
    data class WebSearch <T : ToolDefinition> (override val definition: T) : ProvidedTool<T>(definition)
}
