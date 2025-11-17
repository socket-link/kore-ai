package link.socket.kore.agents.messages

enum class MessageThreadStatus {
    OPEN,
    WAITING_FOR_HUMAN,
    RESOLVED;

    // Validation function to check if ThreadStatus transition is valid
    fun canTransitionTo(newStatus: MessageThreadStatus): Boolean = when (this) {
        OPEN -> when (newStatus) {
            OPEN, WAITING_FOR_HUMAN, RESOLVED -> true
        }
        WAITING_FOR_HUMAN -> when (newStatus) {
            OPEN, WAITING_FOR_HUMAN, RESOLVED -> true
        }
        RESOLVED -> newStatus == RESOLVED // terminal state
    }
}
