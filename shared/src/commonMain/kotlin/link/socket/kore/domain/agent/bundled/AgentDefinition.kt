package link.socket.kore.domain.agent.bundled

import link.socket.kore.domain.agent.AgentInput
import link.socket.kore.domain.ai.configuration.AIConfiguration
import link.socket.kore.domain.ai.configuration.AIConfigurationFactory
import link.socket.kore.domain.chat.Instructions

sealed interface AgentDefinition {

    val name: String
    val description: String
    val suggestedAIConfigurationBuilder: AIConfigurationFactory.() -> AIConfiguration

    val prompt: String
    val requiredInputs: List<AgentInput>
    val optionalInputs: List<AgentInput>

    val instructions: Instructions
        get() = Instructions(
            prompt = prompt,
        )

    sealed class Bundled(
        override val name: String,
        override val description: String,
        override val suggestedAIConfigurationBuilder: AIConfigurationFactory.() -> AIConfiguration,
        override val prompt: String = "",
        override val requiredInputs: List<AgentInput> = emptyList(),
        override val optionalInputs: List<AgentInput> = emptyList(),
    ) : AgentDefinition

    abstract class Custom(
        override val name: String,
        override val description: String,
        override val suggestedAIConfigurationBuilder: (AIConfigurationFactory) -> AIConfiguration,
        override val prompt: String = "",
        override val requiredInputs: List<AgentInput> = emptyList(),
        override val optionalInputs: List<AgentInput> = emptyList(),
    ) : AgentDefinition
}
