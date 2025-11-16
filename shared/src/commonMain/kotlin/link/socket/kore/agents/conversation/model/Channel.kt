package link.socket.kore.agents.conversation.model

typealias ChannelId = String

sealed interface Channel {

    sealed class Public(
        val id: ChannelId,
    ) : Channel {
        data object Engineering : Public("engineering")
        data object Design : Public("design")
        data object Product : Public("product")

        override fun toDisplayString(): String = "#$id"
    }

    data class DirectMessage(
        val fromAgent: Sender.Agent,
    ) : Channel {
        override fun toDisplayString(): String =
            "${fromAgent.agentId}@me"
    }

    // Function to format channel name as displayable string (e.g., "#engineering")
    fun toDisplayString(): String
}
