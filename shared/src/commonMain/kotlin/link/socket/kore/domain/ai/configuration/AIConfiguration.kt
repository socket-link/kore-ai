package link.socket.kore.domain.ai.configuration

import link.socket.kore.domain.ai.model.AIModel
import link.socket.kore.domain.ai.provider.AIProvider

interface AIConfiguration {
    val provider: AIProvider<*, *>
    val model: AIModel?

    // TODO: Change return type into data class
    fun getAvailableModels(): List<Pair<AIProvider<*, *>, AIModel>>
}
