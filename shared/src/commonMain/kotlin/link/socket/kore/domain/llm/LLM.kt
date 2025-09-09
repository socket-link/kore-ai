@file:Suppress("ClassName")

package link.socket.kore.domain.llm

import com.aallam.openai.api.model.ModelId
import link.socket.kore.domain.model.ModelFeatures
import link.socket.kore.domain.limits.ModelLimits
import link.socket.kore.domain.tool.ToolDefinition

fun LLM<*>.toModelId(): ModelId =
    ModelId(name)

sealed class LLM <TD : ToolDefinition>(
    open val name: String,
    open val displayName: String,
    open val description: String,
    open val features: ModelFeatures,
    open val limits: ModelLimits,
)
