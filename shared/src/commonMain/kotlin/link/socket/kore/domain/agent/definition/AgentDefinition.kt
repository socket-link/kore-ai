package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.agent.AgentInput
import link.socket.kore.domain.chat.system.Instructions
import link.socket.kore.domain.ai.configuration.AI_Configuration

sealed interface AgentDefinition {
    val name: String
    val description: String
    val prompt: String
    val aiConfiguration: AI_Configuration

    val neededInputs: List<AgentInput>
    val optionalInputs: List<AgentInput>

    val instructions: Instructions
        get() =
            Instructions(
                prompt = prompt,
            )

    sealed class Bundled(
        override val name: String,
        override val description: String,
        override val prompt: String,
        override val aiConfiguration: AI_Configuration,
        override val neededInputs: List<AgentInput> = emptyList(),
        override val optionalInputs: List<AgentInput> = emptyList(),
    ) : AgentDefinition

    abstract class Custom(
        override val name: String,
        override val description: String,
        override val prompt: String,
        override val aiConfiguration: AI_Configuration,
        override val neededInputs: List<AgentInput> = emptyList(),
        override val optionalInputs: List<AgentInput> = emptyList(),
    ) : AgentDefinition
}
