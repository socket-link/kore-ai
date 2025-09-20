package link.socket.kore.domain.config

import link.socket.kore.domain.ai.AI
import link.socket.kore.domain.llm.LLM

data class AI_ConfigurationWithFallbacks(
    val configurations: List<AI_Configuration>,
) : AI_Configuration {

    private val mainConfiguration = configurations.first()
    private val backupConfigurationProvider = configurations.getOrNull(1)
    private val secondBackupConfigurationProvider = configurations.getOrNull(2)

    private var usedBackupConfiguration = false
    private var usedSecondBackupConfiguration = false

    override val aiProvider: AI<*, *>
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

    // TODO: Move return type into data class
    fun getSuggestedModels(): List<Pair<AI<*, *>, LLM<*>>> {
        return configurations.mapNotNull { configuration ->
            configuration.selectedLLM?.let { llm ->
                configuration.aiProvider to llm
            }
        }
    }
}
