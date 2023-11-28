package link.socket.kore.model.agent

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.TextContent
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.chat.ToolCall
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.serialization.json.JsonObject
import link.socket.kore.model.chat.ChatHistory
import kotlin.reflect.KFunction1

typealias FunctionDefinition = Pair<KFunction1<JsonObject, String>, Tool>

private const val MODEL_NAME = "gpt-4-1106-preview"

interface LLMAgent {
    val openAI: OpenAI
    val instructions: String
    val initialPrompt: String

    val availableFunctions: Map<String, FunctionDefinition>
        get() = emptyMap()

    val initialSystemMessage
        get() = ChatMessage(
            role = ChatRole.System,
            content = instructions,
        )

    val initialPromptMessage
        get() = ChatMessage(
            role = ChatRole.User,
            content = initialPrompt,
        )

    val tools: List<Tool>
        get() = availableFunctions.map { entry ->
            Tool.function(
                name = entry.value.second.function.name,
                description = entry.value.second.description,
                parameters = entry.value.second.function.parameters,
            )
        }.toList()

    var chatHistory: ChatHistory
    var completionRequest: ChatCompletionRequest?

    suspend fun initialize() {
        chatHistory = ChatHistory.NonThreaded(listOf(initialSystemMessage, initialPromptMessage))
        updateCompletionRequest()
    }

    suspend fun execute(): Boolean =
        completionRequest?.let { request ->
            val completion = openAI.chatCompletion(request)
            val response = completion.choices.first()

            updateChatHistory(response.message)

            return if (response.finishReason == FinishReason.ToolCalls) {
                response.message.executePendingToolCalls()
                true
            } else {
                false
            }
        } ?: error("No CompletionRequest found to execute")

    suspend fun ChatMessage.executePendingToolCalls() {
        toolCalls?.forEach { call ->
            when (call) {
                is ToolCall.Function -> {
                    val functionResponse = call.function.execute()
                    updateChatHistory(call.function, functionResponse)
                }
            }
        }
    }

    fun FunctionCall.execute(): String {
        val functionTool = availableFunctions[name]
            ?: error("Function $name not found")

        val functionArgs = argumentsAsJson()

        return functionTool.first.invoke(functionArgs) as? String
            ?: error("Function $name did not return String")
    }

    fun getChatHistoryStrings(): List<String> =
        chatHistory.getMessages()
            .filter { it.role == Role.Assistant || it.role == Role.User }
            .filter { it.messageContent is TextContent }
            .filter { it.content?.isNotEmpty() == true }
            .map { "${it.role.role}:\n ${it.content}\n" }

    fun logChatHistory() {
        getChatHistoryStrings().forEach(::println)
    }

    private fun updateChatHistory(chatMessage: ChatMessage) {
        chatHistory = chatHistory.appendMessage(chatMessage)
        updateCompletionRequest()
    }

    private fun updateChatHistory(function: FunctionCall, response: String) {
        chatHistory = chatHistory.appendFunctionCallResponse(function, response)
        updateCompletionRequest()
    }

    private fun updateCompletionRequest() {
        completionRequest = ChatCompletionRequest(
            model = ModelId(MODEL_NAME),
            messages = chatHistory.getMessages(),
            tools = tools,
        )
    }
}
