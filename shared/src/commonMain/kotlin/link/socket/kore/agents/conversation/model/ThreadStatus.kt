package link.socket.kore.agents.conversation.model

enum class ThreadStatus {
    OPEN,
    WAITING_FOR_HUMAN,
    RESOLVED;

    // Validation function to check if ThreadStatus transition is valid
    fun canTransitionTo(newStatus: ThreadStatus): Boolean = when (this) {
        OPEN -> when (newStatus) {
            OPEN, WAITING_FOR_HUMAN, RESOLVED -> true
        }
        WAITING_FOR_HUMAN -> when (newStatus) {
            OPEN, WAITING_FOR_HUMAN, RESOLVED -> true
        }
        RESOLVED -> newStatus == RESOLVED // terminal state
    }
}
