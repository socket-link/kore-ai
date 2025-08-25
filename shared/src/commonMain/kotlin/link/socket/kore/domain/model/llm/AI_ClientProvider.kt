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

private val ANTHROPIC_API_ENDPOINT = "https://api.anthropic.com/v1/"
private val GOOGLE_API_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/openai/"

sealed interface AI_ClientProvider <
    TD : ToolDefinition,
    L : LLM<TD>> {

    fun provideClient(): Client

    data object Anthropic : AI_ClientProvider<Tool_Claude, LLM_Claude> {
        override fun provideClient(): Client =
            Client(
                token = KotlinConfig.anthropic_api_key,
                logging = LoggingConfig(
                    logLevel = LogLevel.All,
                    logger = Logger.Simple,
                ),
                timeout = Timeout(socket = 1.hours),
                host = OpenAIHost(
                    baseUrl = ANTHROPIC_API_ENDPOINT,
                )
            )
    }

    data object Google : AI_ClientProvider<Tool_Gemini, LLM_Gemini> {
        override fun provideClient(): Client =
            Client(
                token = KotlinConfig.google_api_key,
                logging = LoggingConfig(
                    logLevel = LogLevel.All,
                    logger = Logger.Simple,
                ),
                timeout = Timeout(socket = 1.hours),
                host = OpenAIHost(
                    baseUrl = GOOGLE_API_ENDPOINT,
                )
            )
    }

    data object OpenAI : AI_ClientProvider<Tool_OpenAI, LLM_ChatGPT> {
        override fun provideClient(): Client =
            Client(
                token = KotlinConfig.openai_api_key,
                logging = LoggingConfig(
                    logLevel = LogLevel.All,
                    logger = Logger.Simple,
                ),
                timeout = Timeout(socket = 1.hours),
            )
    }
}
