package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.agent.AgentInput
import link.socket.kore.domain.chat.system.Instructions
import link.socket.kore.domain.chat.system.Seriousness
import link.socket.kore.domain.chat.system.Tone
import link.socket.kore.domain.model.llm.AI_Configuration
import link.socket.kore.domain.model.llm.DEFAULT_AI_CONFIGURATION
import link.socket.kore.domain.model.llm.LLM
import link.socket.kore.domain.model.tool.ToolDefinition

typealias AgentConfig = AI_Configuration<out ToolDefinition, out LLM<out ToolDefinition>>

sealed interface AgentDefinition {
    val name: String
    val prompt: String
    val tone: Tone
    val seriousness: Seriousness

    val neededInputs: List<AgentInput>
    val optionalInputs: List<AgentInput>

    val aiConfiguration: AI_Configuration<out ToolDefinition, out LLM<out ToolDefinition>>

    val instructions: Instructions
        get() =
            Instructions(
                prompt = prompt,
                tone = tone,
                seriousness = seriousness,
            )

    sealed class Bundled(
        override val name: String,
        override val prompt: String,
        override val tone: Tone = Tone.PROFESSIONAL,
        override val seriousness: Seriousness = Seriousness.SOMEWHAT,
        override val neededInputs: List<AgentInput> = emptyList(),
        override val optionalInputs: List<AgentInput> = emptyList(),
        override val aiConfiguration: AgentConfig = DEFAULT_AI_CONFIGURATION,
    ) : AgentDefinition

    abstract class Custom(
        override val name: String,
        override val prompt: String,
        override val neededInputs: List<AgentInput> = emptyList(),
        override val optionalInputs: List<AgentInput> = emptyList(),
        override val aiConfiguration: AgentConfig = DEFAULT_AI_CONFIGURATION,
    ) : AgentDefinition
}
