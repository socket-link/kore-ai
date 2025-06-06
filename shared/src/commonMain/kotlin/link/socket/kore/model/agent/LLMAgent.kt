package link.socket.kore.model.agent

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionCall
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.chat.ToolCall
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import link.socket.kore.model.chat.Chat
import link.socket.kore.model.conversation.ConversationHistory
import link.socket.kore.model.conversation.ConversationId
import link.socket.kore.model.tool.FunctionDefinition
import link.socket.kore.model.tool.FunctionProvider
import link.socket.kore.util.logWith

/**
 * Abstract class representing an Agent that interacts with an LLM.
 */
interface LLMAgent {

    companion object {
        private const val MODEL_NAME = "gpt-4o"
        private val MODEL_ID = ModelId(MODEL_NAME)
    }

    val tag: String
        get() = "LLMAgent"

    val openAI: OpenAI
    val scope: CoroutineScope

    /**
     * Base-level System Prompt instructing the AI Agent about its roles and responsibilities.
     * This will be refined over time with improved prompt engineering techniques.
     *
     * 2024/08/28: `multi_tool_use.parallel` function blocking - https://community.openai.com/t/model-tries-to-call-unknown-function-multi-tool-use-parallel/490653/35
     */
    val prompt: String
        get() = """
            You are an AI Agent operating within the KoreAI library, designed to facilitate specialized interactions between developers and end-users. Your primary role is to leverage your domain-specific knowledge to assist users in solving well-defined tasks.

            There are two types of humans you will interact with:
            1. **Developers**: They configure your parameters and initialize chat sessions. They are responsible for setting up your environment and providing you with specific instructions.
            2. **Users**: They engage with you during chat sessions, seeking your expertise to address their queries and tasks.

            Your responses should be concise and focused on the task at hand. Avoid providing detailed explanations unless explicitly instructed to do so. If a User's query falls outside your area of expertise, guide them towards utilizing your specialized skills.

            As a specialized Agent, you are equipped with specific tools and functions to enhance your capabilities. 
            
            You should always start a conversation with the User by:
            - Asking a relevant question based on your specialized instructions.
            - Offering suggestions to the User about which of your capabilities might be able to assist them, based upon 

            Remember, your primary goal is to assist users efficiently while adhering to the guidelines provided by developers.
            
            You shall only use function calling to invoke the defined functions that have been provided to you.
            **You should NEVER invent or use functions NOT defined or NOT listed by that Agent, especially the multi_tool_use.parallel function.**
        """.trimIndent()

    fun initialSystemMessage(conversationId: ConversationId): Chat.System =
        """
            {
                "conversationId": "$conversationId"
            }
        """.trimIndent().let { metadata ->
            Chat.System("$metadata\n\n$prompt")
        }

    /**
     * @return Map of every available [FunctionProvider], can be extended in concrete implementations
     */
    val availableFunctions: Map<String, FunctionProvider>
        get() = emptyMap()

    /**
     * @return List of Tools derived from [availableFunctions]
     */
    val tools: List<Tool>
        get() =
            availableFunctions.map { entry ->
                entry.value.definition.tool
            }.toList()

    /**
     * Executes a given ChatCompletionRequest and returns a pair containing the chat response and
     * a boolean indicating if there were tool calls pending execution.
     *
     * @param completionRequest - the request to be processed by OpenAI's chat model
     * @param onNewChats - a lambda that is executed whenever new Chats are added to the conversation
     * @return A boolean indicating if Tool calls were ran during this execution
     */
    suspend fun execute(
        completionRequest: ChatCompletionRequest,
        onNewChats: (List<Chat>) -> Unit,
    ): Boolean {
        val completion = openAI.chatCompletion(completionRequest)
        val response = completion.choices.first()

        val completionChat =
            Chat.Text(
                role = response.message.role,
                content = response.message.content ?: "",
            )
        onNewChats(listOf(completionChat))

        return if (response.finishReason == FinishReason.ToolCalls) {
            val toolChats = response.message.executePendingToolCalls()
            onNewChats(toolChats)
            logWith("$tag-execute").i("Response:\n$response\n$toolChats")
            true
        } else {
            logWith("$tag-execute").i("Response:\n$response")
            false
        }
    }

    /**
     * Executes any pending tool calls in the given ChatMessage and returns the responses as a list
     *
     * @return The resulting Function Chat objects
     */
    suspend fun ChatMessage.executePendingToolCalls(): List<Chat> {
        val jobs =
            toolCalls?.map { call ->
                scope.async {
                    when (call) {
                        is ToolCall.Function ->
                            call.function.let { function ->
                                logWith("$tag-executePendingToolCalls").i(
                                    "Executing ${function.name} with Args:\n${function.arguments}",
                                )
                                function.execute().also { response ->
                                    logWith("$tag-executePendingToolCalls").i(
                                        "Tool ${function.name} Response: $response",
                                    )
                                }
                            }
                    }
                }
            } ?: emptyList()

        return jobs.awaitAll()
    }

    /**
     * Executes the function call and returns the response as a Chat object
     *
     * @return Chat response from the function call execution
     */
    suspend fun FunctionCall.execute(): Chat {
        val functionTool = availableFunctions[name] ?: error("Function $name not found")

        val functionArgs = argumentsAsJson()

        return when (val definition = functionTool.definition) {
            is FunctionDefinition.StringReturn -> {
                val content = definition.execute(functionArgs)

                Chat.Text(
                    role = ChatRole.Function,
                    functionName = nameOrNull,
                    content = content,
                )
            }
            is FunctionDefinition.CSVReturn -> {
                val content = definition(functionArgs)

                Chat.CSV(
                    role = ChatRole.Function,
                    functionName = nameOrNull,
                    csvContent = content,
                )
            }
        }
    }

    /**
     * Creates a ChatCompletionRequest object with the given conversation history
     *
     * @param conversationHistory - the history of the conversation to include in the request
     * @return ChatCompletionRequest ready to be sent to the OpenAI API
     */
    fun createCompletionRequest(conversationHistory: ConversationHistory): ChatCompletionRequest =
        ChatCompletionRequest(
            model = MODEL_ID,
            messages = conversationHistory.getChats().map { it.chatMessage },
            tools = tools.ifEmpty { null },
            topP = 0.1,
        )
}
