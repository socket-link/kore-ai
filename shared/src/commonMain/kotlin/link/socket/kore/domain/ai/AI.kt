@file:Suppress("ClassName")

package link.socket.kore.domain.ai

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI as Client
import com.aallam.openai.client.OpenAIHost
import kotlin.time.Duration.Companion.hours
import link.socket.kore.domain.llm.LLM
import link.socket.kore.domain.llm.LLM_Claude
import link.socket.kore.domain.llm.LLM_Gemini
import link.socket.kore.domain.tool.ToolDefinition

typealias ProviderId = String

val DEFAULT_AI_CONFIGURATION = aiConfiguration(
    LLM_Gemini.Flash_2_5,
    aiConfiguration(LLM_Claude.Sonnet_3_7),
)

sealed interface AI<
    TD : ToolDefinition,
    L : LLM<TD>> {

    val id: ProviderId
    val name: String
    val defaultModel: L
    val availableModels: List<L>
    val apiToken: String

    val client: Client

    companion object Companion {
        val ALL_PROVIDERS = listOf(
            AI_Anthropic,
            AI_Google,
            AI_OpenAI,
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
