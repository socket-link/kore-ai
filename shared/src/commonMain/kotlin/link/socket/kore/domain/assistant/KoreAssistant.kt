package link.socket.kore.domain.assistant

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.Assistant
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.assistant.Function
import com.aallam.openai.api.core.Parameters
import com.aallam.openai.api.model.ModelId
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.domain.agent.LLMAgent
import link.socket.kore.domain.chat.Chat
import link.socket.kore.domain.model.ai.configuration.AI_Configuration
import link.socket.kore.domain.model.tool.FunctionProvider

@OptIn(BetaOpenAI::class)
data class KoreAssistant(
    override val config: AI_Configuration,
    override val scope: CoroutineScope,
    private val existingAssistantId: AssistantId?,
    private val modelId: ModelId,
    private val name: String,
    override val prompt: String,
    override val availableFunctions: Map<String, FunctionProvider>,
) : LLMAgent {

    override val tag: String = "KoreAssistant${name.replace(" ", "")}-${super.tag}"

    private val assistantTools: List<AssistantTool> =
        tools.map { tool ->
            AssistantTool.FunctionTool(
                Function(
                    name = tool.function.name,
                    description = tool.function.description
                        ?: error("Function ${tool.function.name} missing description"),
                    parameters = tool.function.parameters ?: Parameters.Empty,
                ),
            )
        } + listOf(
            AssistantTool.CodeInterpreter,
        )

    private lateinit var assistant: Assistant

    suspend fun initialize(initialMessage: Chat?) {
        // TODO: Query for existing assistant if `assistantId` is passed
    }
}
