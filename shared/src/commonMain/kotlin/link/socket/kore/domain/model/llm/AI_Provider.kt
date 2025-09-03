package link.socket.kore.domain.model.llm

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI as Client
import com.aallam.openai.client.OpenAIHost
import kotlin.time.Duration.Companion.hours
import link.kore.shared.config.KotlinConfig
import link.socket.kore.domain.model.tool.ToolDefinition
import link.socket.kore.domain.model.tool.Tool_Claude
import link.socket.kore.domain.model.tool.Tool_Gemini
import link.socket.kore.domain.model.tool.Tool_OpenAI

private const val ANTHROPIC_API_ENDPOINT = "https://api.anthropic.com/v1/"
private const val GOOGLE_API_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/openai/"

typealias ProviderId = String

sealed interface AI_Provider <
    TD : ToolDefinition,
    L : LLM<TD>> {

    val id: ProviderId
    val name: String
    val defaultModel: L
    val availableModels: List<L>

    val client: Client

    data object Anthropic : AI_Provider<Tool_Claude, LLM_Claude> {
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

    data object Google : AI_Provider<Tool_Gemini, LLM_Gemini> {
        override val id: ProviderId = "google"
        override val name: String = "Google"

        override val defaultModel: LLM_Gemini by lazy {
            LLM_Gemini.DEFAULT
        }

        override val availableModels: List<LLM_Gemini> by lazy {
            LLM_Gemini.ALL_MODELS
        }

        override val client: Client by lazy {
            createClient(
                token = KotlinConfig.google_api_key,
                url = GOOGLE_API_ENDPOINT,
            )
        }
    }

    data object OpenAI : AI_Provider<Tool_OpenAI, LLM_ChatGPT> {
        override val id: ProviderId = "openai"
        override val name: String = "OpenAI"

        override val defaultModel: LLM_ChatGPT by lazy {
            LLM_ChatGPT.DEFAULT
        }

        override val availableModels: List<LLM_ChatGPT> by lazy {
            LLM_ChatGPT.ALL_MODELS
        }

        override val client: Client by lazy {
            createClient(
                token = KotlinConfig.openai_api_key,
            )
        }
    }

    companion object {
        val ALL_PROVIDERS = listOf(
            Anthropic,
            Google,
            OpenAI,
        )

        fun createClient(
            token: String,
            url: String? = null,
        ): Client = Client(
            token = token,
            logging = LoggingConfig(
                logLevel = LogLevel.All,
                logger = Logger.Simple,
            ),
            timeout = Timeout(socket = 1.hours),
            host = url?.let { hostUrl ->
                OpenAIHost(
                    baseUrl = hostUrl,
                )
            } ?: OpenAIHost.OpenAI
        )
    }
}
