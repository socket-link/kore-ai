@file:Suppress("ClassName")

package link.socket.kore.domain.model.llm

import link.socket.kore.domain.model.tool.ToolDefinition

sealed class LLM <TD : ToolDefinition>(
    open val name: String,
    open val features: ModelFeatures,
)
