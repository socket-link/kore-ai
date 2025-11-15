package link.socket.kore.domain.ai.provider

import com.aallam.openai.client.OpenAI as Client
import link.kore.shared.config.KotlinConfig
import link.socket.kore.domain.ai.model.AIModel_Claude
import link.socket.kore.domain.tool.AITool_Claude

private const val ID = "anthropic"
private const val NAME = "Anthropic"
private const val ANTHROPIC_API_ENDPOINT = "https://api.anthropic.com/v1/"

data object AIProvider_Anthropic : AIProvider<AITool_Claude, AIModel_Claude> {

    override val id: ProviderId = ID
    override val name: String = NAME
    override val apiToken: String = KotlinConfig.anthropic_api_key
    override val availableModels: List<AIModel_Claude> = AIModel_Claude.ALL_MODELS

    override val client: Client by lazy {
        AIProvider.createClient(
            token = apiToken,
            url = ANTHROPIC_API_ENDPOINT,
        )
    }
}
