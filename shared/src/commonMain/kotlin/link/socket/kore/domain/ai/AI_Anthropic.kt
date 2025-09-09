package link.socket.kore.domain.ai

import com.aallam.openai.client.OpenAI as Client
import link.kore.shared.config.KotlinConfig
import link.socket.kore.domain.ai.AI.Companion.createClient
import link.socket.kore.domain.ai.configuration.AI_Configuration
import link.socket.kore.domain.ai.configuration.AI_ConfigurationWithFallback
import link.socket.kore.domain.ai.configuration.AI_ConfigurationStandard
import link.socket.kore.domain.llm.LLM_Claude
import link.socket.kore.domain.tool.Tool_Claude

private const val ANTHROPIC_API_ENDPOINT = "https://api.anthropic.com/v1/"

data object AI_Anthropic : AI<Tool_Claude, LLM_Claude> {
    override val id: ProviderId = "anthropic"
    override val name: String = "Anthropic"

    override val defaultModel: LLM_Claude by lazy {
        LLM_Claude.DEFAULT
    }

    override val availableModels: List<LLM_Claude> by lazy {
        LLM_Claude.ALL_MODELS
    }

    override val client: Client by lazy {
        createClient(
            token = KotlinConfig.anthropic_api_key,
            url = ANTHROPIC_API_ENDPOINT,
        )
    }
}

fun aiConfiguration(
    model: LLM_Claude,
    vararg backups: AI_Configuration,
): AI_ConfigurationWithFallback = AI_ConfigurationWithFallback(
    configurations = AI_ConfigurationStandard(
        aiProvider = AI_Anthropic,
        selectedLLM = model,
    ).let(::listOf) + backups.toList(),
)
