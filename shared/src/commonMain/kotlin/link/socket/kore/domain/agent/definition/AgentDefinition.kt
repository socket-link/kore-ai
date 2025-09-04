package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.agent.AgentInput
import link.socket.kore.domain.chat.system.Instructions
import link.socket.kore.domain.model.llm.AI_Configuration
import link.socket.kore.domain.model.llm.LLM
import link.socket.kore.domain.model.tool.ToolDefinition

typealias AgentConfig = AI_Configuration<out ToolDefinition, out LLM<out ToolDefinition>>

sealed interface AgentDefinition {
    val name: String
    val prompt: String
    val aiConfiguration: AI_Configuration<out ToolDefinition, out LLM<out ToolDefinition>>

    val neededInputs: List<AgentInput>
    val optionalInputs: List<AgentInput>

    val instructions: Instructions
        get() =
            Instructions(
                prompt = prompt,
            )

    sealed class Bundled(
        override val name: String,
        override val prompt: String,
        override val aiConfiguration: AgentConfig,
        override val neededInputs: List<AgentInput> = emptyList(),
        override val optionalInputs: List<AgentInput> = emptyList(),
    ) : AgentDefinition

    abstract class Custom(
        override val name: String,
        override val prompt: String,
        override val aiConfiguration: AgentConfig,
        override val neededInputs: List<AgentInput> = emptyList(),
        override val optionalInputs: List<AgentInput> = emptyList(),
    ) : AgentDefinition
}
