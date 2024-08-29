package link.socket.kore.model.conversation

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.thread.ThreadId
import link.socket.kore.model.chat.Chat

/**
 * Represents the contract for managing conversation histories.
 * This sealed interface encompasses both threaded and non-threaded conversation types,
 * allowing for flexible handling of chat messages across various conversational contexts.
 */
@OptIn(BetaOpenAI::class)
sealed interface ConversationHistory {

    /**
     * Represents a threaded conversation history, characterized by multiple threads of messages.
     * Contains an ID for the associated assistant and a map linking thread IDs to their respective chat messages.
     */
    sealed class Threaded(
        open val assistantId: AssistantId?, // The ID of the assistant tied to this conversation.
        open val chatMessages: Map<ThreadId, List<Chat>>, // A mapping of thread IDs and their associated chat messages.
    ) : ConversationHistory {

        /**
         * Represents an uninitialized threaded conversation, where no messages have been added.
         * All operations related to message appending will lead to an error, as this state is not ready to hold messages.
         */
        data object Uninitialized : Threaded(null, emptyMap()) {

            /**
             * Returns an empty list of Chats in an uninitialized conversation state.
             * This represents a state before any messages have been added to the conversation.
             */
            override fun getChats(): List<Chat> = emptyList()

            override fun appendMessage(message: ChatMessage): Uninitialized {
                error("Attempt to appendMessage $message to ChatHistory.Threaded.Uninitialized")
            }

            /**
             * Appends a Chat to the conversation history.
             * This operation is not implemented, as uninitialized states cannot hold messages yet.
             */
            override fun appendChat(message: Chat): ConversationHistory {
                TODO("Not yet implemented")
            }
        }

        /**
         * Represents a fully initialized threaded conversation history, containing valid chat messages organized by thread.
         * Enables the retrieval and manipulation of chat messages within this structured context.
         *
         * @param assistantId The ID of the assistant associated with the conversation.
         * @param chatMessages A map linking thread IDs to lists of chat messages within this conversation.
         */
        data class Initialized(
            override val assistantId: AssistantId, // The assistant ID for this conversation.
            override val chatMessages: Map<ThreadId, List<Chat>>, // The initialized map of chat messages organized by thread ID.
        ) : Threaded(assistantId, chatMessages) {

            /**
             * Returns all Chats associated with the initialized conversation, aggregating messages from all threads.
             *
             * @return A list of all Chats in the conversation.
             */
            override fun getChats(): List<Chat> {
                TODO("Not yet implemented")
            }

            /**
             * Appends a Chat to the list of messages for this conversation and returns the updated conversation history.
             *
             * @param message The Chat object to be appended.
             * @return The updated conversation history after appending the Chat object.
             */
            override fun appendChat(message: Chat): ConversationHistory {
                TODO("Not yet implemented")
            }

            /**
             * Appends a ChatMessage (from an API response) to the chat history
             * and returns an updated instance of Initialized representing the new state.
             *
             * @param message The ChatMessage to append.
             * @return The updated conversation history.
             */
            override fun appendMessage(message: ChatMessage): Initialized {
                // TODO: Handle threaded message appending logic
                return this
            }
        }
    }

    /**
     * Represents a non-threaded conversation history, organized as a simple list of chat messages.
     * Supports basic operations such as message retrieval and appending within a flat conversational structure.
     *
     * @param messages A list of chat messages in the conversation.
     */
    data class NonThreaded(
        val messages: List<Chat>, // A simple list of chat messages in the conversation.
    ) : ConversationHistory {

        /**
         * Retrieves all Chat messages from the NonThreaded conversation.
         *
         * @return A list of all Chat messages.
         */
        override fun getChats(): List<Chat> = messages

        /**
         * Appends a Chat message to the existing list and returns an updated NonThreaded conversation history.
         *
         * @param message The Chat object to be appended.
         * @return The updated conversation history after appending the Chat object.
         */
        override fun appendChat(message: Chat): ConversationHistory =
            NonThreaded(messages.append(message))

        /**
         * Appends a ChatMessage (received from the API) to the list and returns an updated NonThreaded conversation instance.
         *
         * @param message The ChatMessage to append.
         * @return The updated NonThreaded conversation after appending the ChatMessage.
         */
        override fun appendMessage(message: ChatMessage): NonThreaded =
            NonThreaded(messages.append(message))
    }

    /**
     * Abstract method to collect and return chat messages from the conversation.
     * Must be implemented by subclasses to define specific retrieval logic.
     *
     * @return A list of chat messages from the conversation.
     */
    fun getChats(): List<Chat>

    /**
     * Abstract method to append a ChatMessage and return the updated conversation history.
     * The implementation is expected to manage the state transition correctly.
     *
     * @param message The ChatMessage to append.
     * @return The updated conversation history after appending the ChatMessage.
     */
    fun appendMessage(message: ChatMessage): ConversationHistory

    /**
     * Abstract method to append a Chat object to the conversation and return the updated state.
     * Implementations must ensure chat integrity and ordering based on the conversation type.
     *
     * @param message The Chat object to be appended.
     * @return The updated conversation history after appending the Chat object.
     */
    fun appendChat(message: Chat): ConversationHistory
}

/**
 * Extension function to append a ChatMessage to a list of Chat.
 * This function expands the existing list into a new mutable list and adds
 * the Chat representation of the ChatMessage to the end of the list, effectively
 * creating a new list with the appended message.
 *
 * @param newMessage The ChatMessage to append.
 * @return A new list of Chat messages including the appended ChatMessage.
 */
fun List<Chat>.append(
    newMessage: ChatMessage,
): List<Chat> =
    mutableListOf(
        *this.toTypedArray(), // Adds the current list
        Chat.Text.fromChatMessage(newMessage), // Converts ChatMessage to Chat.Text and appends to list.
    )

/**
 * Extension function to append a Chat object to a list of Chat.
 * Similar to the previous function but for direct Chat instances.
 *
 * @param newMessage The Chat object to append.
 * @return A new list of Chat messages including the appended Chat object.
 */
fun List<Chat>.append(
    newMessage: Chat
): List<Chat> =
    mutableListOf(
        *this.toTypedArray(), // Adds the current list
        newMessage, // Appends the new Chat object to the list.
    )
