@file:Suppress("ClassName")

package link.socket.kore.domain.llm

import ai.koog.prompt.executor.clients.anthropic.AnthropicModels
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.llm.LLModel
import com.aallam.openai.api.model.ModelId
import link.socket.kore.domain.limits.ModelLimits
import link.socket.kore.domain.model.ModelFeatures
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

fun LLM<*>.toKoogLLMModel(): LLModel = when (this) {
    is LLM_OpenAI -> when (this) {
        is LLM_OpenAI.GPT_5 -> OpenAIModels.Chat.GPT5
        is LLM_OpenAI.GPT_5_mini -> OpenAIModels.Chat.GPT5Mini
        is LLM_OpenAI.GPT_5_nano -> OpenAIModels.Chat.GPT5Nano
        is LLM_OpenAI.GPT_4_1 -> OpenAIModels.Chat.GPT4_1
        is LLM_OpenAI.GPT_4_1_mini -> OpenAIModels.CostOptimized.GPT4_1Mini
        is LLM_OpenAI.GPT_4o -> OpenAIModels.Chat.GPT4o
        is LLM_OpenAI.GPT_4o_mini -> OpenAIModels.CostOptimized.GPT4oMini
        is LLM_OpenAI.o4_mini -> OpenAIModels.Reasoning.O4Mini
        is LLM_OpenAI.o3-> OpenAIModels.Reasoning.O3
        is LLM_OpenAI.o3_mini -> OpenAIModels.Reasoning.O3Mini
    }
    is LLM_Gemini -> when (this) {
        is LLM_Gemini.Pro_2_5 -> GoogleModels.Gemini2_5Pro
        is LLM_Gemini.Flash_2_5 -> GoogleModels.Gemini2_5Flash
        is LLM_Gemini.Flash_Lite_2_5 -> GoogleModels.Gemini2_5FlashLite
        is LLM_Gemini.Flash_2_0 -> GoogleModels.Gemini2_0Flash
        is LLM_Gemini.Flash_Lite_2_0 -> GoogleModels.Gemini2_0FlashLite
    }
    is LLM_Claude -> when (this) {
        is LLM_Claude.Opus_4_1 -> AnthropicModels.Opus_4_1
        is LLM_Claude.Opus_4 -> AnthropicModels.Opus_4
        is LLM_Claude.Sonnet_4 -> AnthropicModels.Sonnet_4
        is LLM_Claude.Sonnet_3_7 -> AnthropicModels.Sonnet_3_7
        is LLM_Claude.Haiku_3_5 -> AnthropicModels.Haiku_3_5
        is LLM_Claude.Haiku_3 -> AnthropicModels.Haiku_3
    }
}
