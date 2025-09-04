package link.socket.kore.domain.model.llm

import com.aallam.openai.client.OpenAI as Client
import link.socket.kore.domain.model.tool.ToolDefinition

val DEFAULT_AI_CONFIGURATION = aiConfiguration(
    model = LLM_Gemini.Flash_2_5,
    backup = aiConfiguration(LLM_Claude.Sonnet_3_7),
)

data class AI_ConfigurationWithFallback(
    val mainConfigurationProvider: AI_Configuration<ToolDefinition, LLM<ToolDefinition>>,
    val backupConfigurationProvider: AI_Configuration<ToolDefinition, LLM<ToolDefinition>>?,
) : AI_Configuration<ToolDefinition, LLM<ToolDefinition>>() {

    private var usedBackupConfiguration = false

    override val client: Client = try {
        mainConfigurationProvider.client
    } catch (e: Exception) {
        e.printStackTrace()

        val client = backupConfigurationProvider?.client
            ?: throw IllegalStateException(
                "Failed to initialize client for ${backupConfigurationProvider?.selectedLLM?.name}",
            )
        usedBackupConfiguration = true
        client
    }

    override val selectedLLM: LLM<ToolDefinition> by lazy {
        if (!usedBackupConfiguration) {
            return@lazy mainConfigurationProvider.selectedLLM
        } else {
            backupConfigurationProvider?.selectedLLM
                ?: throw IllegalStateException(
                    "Failed to initialize LLM for ${backupConfigurationProvider?.selectedLLM?.name}",
                )
        }
    }
}

/** Create a configuration for Google Gemini models. */
fun aiConfiguration(
    model: LLM_Gemini,
    backup: AI_Configuration<ToolDefinition, LLM<ToolDefinition>>? = null,
): AI_Configuration<ToolDefinition, LLM<ToolDefinition>> = AI_ConfigurationWithFallback(
    mainConfigurationProvider = StandardAI_Configuration(
        client = AI_Provider._Google.client,
        selectedLLM = model as LLM<ToolDefinition>
    ),
    backupConfigurationProvider = backup,
)

/** Create a configuration for Anthropic Claude models. */
fun aiConfiguration(
    model: LLM_Claude,
    backup: AI_Configuration<ToolDefinition, LLM<ToolDefinition>>? = null,
): AI_ConfigurationWithFallback = AI_ConfigurationWithFallback(
    mainConfigurationProvider = StandardAI_Configuration(
        client = AI_Provider._Anthropic.client,
        selectedLLM = model as LLM<ToolDefinition>
    ),
    backupConfigurationProvider = backup,
)

/**
 * Create a configuration for OpenAI models.
 */
fun aiConfiguration(
    model: LLM_OpenAI,
    backup: AI_Configuration<ToolDefinition, LLM<ToolDefinition>>? = null,
): AI_ConfigurationWithFallback = AI_ConfigurationWithFallback(
    mainConfigurationProvider = StandardAI_Configuration(
        client = AI_Provider._OpenAI.client,
        selectedLLM = model as LLM<ToolDefinition>
    ),
    backupConfigurationProvider = backup,
)
