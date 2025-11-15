package link.socket.kore.agents.core

/**
 * Core foundational types for autonomous agents. All types are immutable and
 * defined in commonMain to support Kotlin Multiplatform targets.
 */

data class Context(
    val currentState: Map<String, Any>,
    val timestamp: Long = 0L
)

/**
 * Represents a high-level plan produced by an agent for a given task.
 *
 * @param steps Ordered list of steps the agent intends to take.
 * @param estimatedComplexity A rough, relative measure of complexity (e.g., 1–10).
 * @param requiresHumanApproval Whether the plan should be approved by a human before execution.
 */
data class Plan(
    val steps: List<String>,
    val estimatedComplexity: Int,
    val requiresHumanApproval: Boolean
)

/**
 * Represents the outcome of an agent action or tool execution.
 *
 * @param success Whether the action succeeded.
 * @param result Optional result payload produced by the action.
 * @param errorMessage Optional error description when success is false.
 */
data class Outcome(
    val success: Boolean,
    val result: Any?,
    val errorMessage: String? = null
)

/**
 * A message that an agent may emit for logging, user communication, or escalation.
 *
 * @param content The human-readable message content.
 * @param severity The severity level of the message.
 * @param requiresResponse Whether a human response is requested/required.
 */
data class Message(
    val content: String,
    val severity: MessageSeverity,
    val requiresResponse: Boolean = false
)

enum class MessageSeverity { INFO, WARNING, ERROR, QUESTION }
