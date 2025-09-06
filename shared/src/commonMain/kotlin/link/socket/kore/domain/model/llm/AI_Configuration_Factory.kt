package link.socket.kore.domain.model.llm

val DEFAULT_AI_CONFIGURATION = aiConfiguration(
    LLM_Gemini.Flash_2_5,
    aiConfiguration(LLM_Claude.Sonnet_3_7),
)

class AI_ConfigurationWithFallback(
    val configurations: List<AI_Configuration>,
) : AI_Configuration() {

    private val mainConfiguration = configurations.first()
    private val backupConfigurationProvider = configurations.getOrNull(1)
    private val secondBackupConfigurationProvider = configurations.getOrNull(2)

    private var usedBackupConfiguration = false
    private var usedSecondBackupConfiguration = false

    override val aiProvider: AI_Provider<*, *>
        get() = try {
            val provider = mainConfiguration.aiProvider
            // Try to invoke an exception
            provider.client
            provider
        } catch (e: Exception) {
            e.printStackTrace()

            try {
                val provider = backupConfigurationProvider?.aiProvider
                    ?: throw IllegalStateException(
                        "Failed to initialize client for ${backupConfigurationProvider?.selectedLLM?.name}",
                    )
                provider.client
                usedBackupConfiguration = true
                provider
            } catch (e: Exception) {
                e.printStackTrace()

                val provider = secondBackupConfigurationProvider?.aiProvider
                    ?: throw IllegalStateException(
                        "Failed to initialize client for ${secondBackupConfigurationProvider?.selectedLLM?.name}",
                    )
                provider.client
                usedSecondBackupConfiguration = true
                provider
            }
        }

    override val selectedLLM: LLM<*>?
        get() = if (usedSecondBackupConfiguration) {
            secondBackupConfigurationProvider?.selectedLLM
                ?: throw IllegalStateException(
                    "Failed to initialize LLM for ${secondBackupConfigurationProvider?.selectedLLM?.name}",
                )
        } else if (usedBackupConfiguration){
            backupConfigurationProvider?.selectedLLM
                ?: throw IllegalStateException(
                    "Failed to initialize LLM for ${backupConfigurationProvider?.selectedLLM?.name}",
                )
        } else {
            mainConfiguration.selectedLLM
        }

    fun getSuggestedModels(): List<Pair<AI_Provider<*, *>, LLM<*>>> {
        return configurations.mapNotNull { configuration ->
            configuration.selectedLLM?.let { llm ->
                configuration.aiProvider to llm
            }
        }
    }
}

/** Create a configuration for Google Gemini models. */
fun aiConfiguration(
    model: LLM_Gemini,
    vararg backup: AI_Configuration,
): AI_ConfigurationWithFallback = AI_ConfigurationWithFallback(
    configurations = StandardAI_Configuration(
        aiProvider = AI_Provider._Google,
        selectedLLM = model,
    ).let(::listOf) + backup.toList(),
)

/** Create a configuration for Anthropic Claude models. */
fun aiConfiguration(
    model: LLM_Claude,
    vararg backup: AI_Configuration,
): AI_ConfigurationWithFallback = AI_ConfigurationWithFallback(
    configurations = StandardAI_Configuration(
        aiProvider = AI_Provider._Anthropic,
        selectedLLM = model,
    ).let(::listOf) + backup.toList(),
)

/**
 * Create a configuration for OpenAI models.
 */
fun aiConfiguration(
    model: LLM_OpenAI,
    vararg backup: AI_Configuration,
): AI_ConfigurationWithFallback = AI_ConfigurationWithFallback(
    configurations = StandardAI_Configuration(
        aiProvider = AI_Provider._OpenAI,
        selectedLLM = model,
    ).let(::listOf) + backup.toList(),
)
