package link.socket.kore.domain.model.ai

import com.aallam.openai.client.OpenAI as Client
import link.kore.shared.config.KotlinConfig
import link.socket.kore.domain.model.ai.AI.Companion.createClient
import link.socket.kore.domain.model.ai.configuration.AI_Configuration
import link.socket.kore.domain.model.ai.configuration.AI_ConfigurationWithFallback
import link.socket.kore.domain.model.ai.configuration.AI_ConfigurationStandard
import link.socket.kore.domain.model.llm.LLM_OpenAI
import link.socket.kore.domain.model.tool.Tool_OpenAI

data object AI_OpenAI : AI<Tool_OpenAI, LLM_OpenAI> {
    override val id: ProviderId = "openai"
    override val name: String = "OpenAI"

    override val defaultModel: LLM_OpenAI by lazy {
        LLM_OpenAI.DEFAULT
    }

    override val availableModels: List<LLM_OpenAI> by lazy {
        LLM_OpenAI.ALL_MODELS
    }

    override val client: Client by lazy {
        createClient(
            token = KotlinConfig.openai_api_key,
        )
    }
}

fun aiConfiguration(
    model: LLM_OpenAI,
    vararg backups: AI_Configuration,
): AI_ConfigurationWithFallback = AI_ConfigurationWithFallback(
    configurations = AI_ConfigurationStandard(
        aiProvider = AI_OpenAI,
        selectedLLM = model,
    ).let(::listOf) + backups.toList(),
)
