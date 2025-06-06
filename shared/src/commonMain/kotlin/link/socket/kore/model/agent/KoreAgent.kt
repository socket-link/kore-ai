@file:Suppress("MemberVisibilityCanBePrivate")

package link.socket.kore.model.agent

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.data.ConversationRepository
import link.socket.kore.model.OPEN_AI
import link.socket.kore.model.capability.AgentCapability
import link.socket.kore.model.capability.IOCapability
import link.socket.kore.model.tool.FunctionProvider

/**
 * Data class representing a KoreAgent, implementing the LLMAgent interface.
 *
 * @property scope - CoroutineScope for managing coroutines
 * @property definition - Definition object that contains the details and instructions for the agent
 * @property conversationRepository - Repository for managing conversation history
 */
data class KoreAgent(
    override val scope: CoroutineScope,
    val definition: AgentDefinition,
    val conversationRepository: ConversationRepository,
) : LLMAgent {

    val name: String = definition.name

    override val tag: String = "Kore${name.replace(" ", "")}-${super.tag}"

    override val openAI: OpenAI = OPEN_AI

    /**
     * Prompt for the Agent, extending the base prompt from LLMAgent
     */
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
                AgentCapability.PromptAgent(tag, conversationRepository, scope).impl,
                IOCapability.ReadFolderContents(tag).impl,
                IOCapability.CreateFile(tag).impl,
                IOCapability.ReadFiles(tag).impl,
                IOCapability.ParseCsv(tag).impl,
            )
}
