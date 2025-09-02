@file:Suppress("MemberVisibilityCanBePrivate")

package link.socket.kore.domain.agent

import kotlinx.coroutines.CoroutineScope
import link.socket.kore.data.ConversationRepository
import link.socket.kore.domain.agent.definition.AgentDefinition
import link.socket.kore.domain.capability.AgentCapability
import link.socket.kore.domain.capability.IOCapability
import link.socket.kore.domain.model.llm.AI_Configuration
import link.socket.kore.domain.model.tool.FunctionProvider

data class KoreAgent(
    override val scope: CoroutineScope,
    override val config: AI_Configuration<*, *>,
    val definition: AgentDefinition,
    val conversationRepository: ConversationRepository,
) : LLMAgent {

    val name: String = definition.name

    override val tag: String = "Kore${name.replace(" ", "")}-${super.tag}"

    override val prompt: String
        get() = """
            ${super.prompt}

            You are an Agent that can provide answers to Chat prompts through LLM completion.
            
            ${definition.instructions.build()}
        """.trimIndent()

    override val availableFunctions: Map<String, FunctionProvider>
        get() =
            mapOf(
                AgentCapability.GetAgents(tag).impl,
                AgentCapability.PromptAgent(
                    agentTag = tag,
                    config = config,
                    conversationRepository = conversationRepository,
                    scope = scope,
                ).impl,
                IOCapability.ReadFolderContents(tag).impl,
                IOCapability.CreateFile(tag).impl,
                IOCapability.ReadFiles(tag).impl,
                IOCapability.ParseCsv(tag).impl,
            )
}
