package link.socket.kore.domain.ai.configuration

import link.socket.kore.domain.ai.model.AIModel
import link.socket.kore.domain.ai.provider.AIProvider

data class AIConfiguration_Default(
    override val provider: AIProvider<*, *>,
    override val model: AIModel?,
) : AIConfiguration {
    override fun getAvailableModels(): List<Pair<AIProvider<*, *>, AIModel>> =
        listOf(provider to (model ?: provider.defaultModel))
}
