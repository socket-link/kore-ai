package link.socket.kore.domain.ai.provider

import com.aallam.openai.client.OpenAI as Client
import link.kore.shared.config.KotlinConfig
import link.socket.kore.domain.ai.model.AIModel_Gemini
import link.socket.kore.domain.tool.AITool_Gemini

private const val ID = "google"
private const val NAME = "Google"
private const val GOOGLE_API_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/openai/"

data object AIProvider_Google : AIProvider<AITool_Gemini, AIModel_Gemini> {

    override val id: ProviderId = ID
    override val name: String = NAME
    override val apiToken: String = KotlinConfig.google_api_key
    override val defaultModel: AIModel_Gemini = AIModel_Gemini.DEFAULT
    override val availableModels: List<AIModel_Gemini> = AIModel_Gemini.ALL_MODELS

    override val client: Client by lazy {
        AIProvider.createClient(
            token = apiToken,
            url = GOOGLE_API_ENDPOINT,
        )
    }
}
