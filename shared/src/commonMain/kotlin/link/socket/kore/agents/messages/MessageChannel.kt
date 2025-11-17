package link.socket.kore.agents.messages

typealias MessageChannelId = String

sealed interface MessageChannel {

    sealed class Public(
        val id: MessageChannelId,
    ) : MessageChannel {
        data object Engineering : Public("#engineering")
        data object Design : Public("#design")
        data object Product : Public("#product")

        override fun getIdentifier(): String = id
    }

    data class Direct(
        val sender: MessageSender.Agent,
    ) : MessageChannel {
        override fun getIdentifier(): String =
            "${sender.agentId}@me"
    }

    // Function to format channel name as displayable string (e.g., "#engineering")
    fun getIdentifier(): String

    companion object {
        fun fromMessageChannelId(id: MessageChannelId): MessageChannel = when (id) {
            Public.Engineering.id -> {
                Public.Engineering
            }
            Public.Design.id -> {
                Public.Design
            }
            Public.Product.id -> {
                Public.Product
            }
            else -> {
                val agentId = id.substringBefore("@")
                Direct(MessageSender.Agent(agentId))
            }
        }
    }
}
