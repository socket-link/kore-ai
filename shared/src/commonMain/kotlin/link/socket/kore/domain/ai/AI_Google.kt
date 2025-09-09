package link.socket.kore.domain.ai

import com.aallam.openai.client.OpenAI as Client
import link.kore.shared.config.KotlinConfig
import link.socket.kore.domain.ai.configuration.AI_Configuration
import link.socket.kore.domain.ai.configuration.AI_ConfigurationWithFallback
import link.socket.kore.domain.ai.configuration.AI_ConfigurationStandard
import link.socket.kore.domain.llm.LLM_Gemini
import link.socket.kore.domain.tool.Tool_Gemini

private const val GOOGLE_API_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/openai/"

data object AI_Google : AI<Tool_Gemini, LLM_Gemini> {
    override val id: ProviderId = "google"
    override val name: String = "Google"

    override val defaultModel: LLM_Gemini by lazy {
        LLM_Gemini.DEFAULT
    }

    override val availableModels: List<LLM_Gemini> by lazy {
        LLM_Gemini.ALL_MODELS
    }

    override val client: Client by lazy {
        AI.createClient(
            token = KotlinConfig.google_api_key,
            url = GOOGLE_API_ENDPOINT,
        )
    }
}

fun aiConfiguration(
    model: LLM_Gemini,
    vararg backups: AI_Configuration,
): AI_ConfigurationWithFallback = AI_ConfigurationWithFallback(
    configurations = AI_ConfigurationStandard(
        aiProvider = AI_Google,
        selectedLLM = model,
    ).let(::listOf) + backups.toList(),
)
