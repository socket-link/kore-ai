package link.socket.kore.agents.tools

import link.socket.kore.agents.core.AutonomyLevel
import link.socket.kore.agents.core.Outcome

/**
 * A safety-valve tool that escalates uncertainty to a human operator.
 * The tool forwards a question to a provided [humanInterface] and returns the
 * response in an [Outcome].
 */
class AskHumanTool(
    private val humanInterface: (String) -> String
) : Tool {
    override val name: String = "ask_human"
    override val description: String = "Escalates uncertainty to human for guidance"
    override val requiredAutonomyLevel: AutonomyLevel = AutonomyLevel.ASK_BEFORE_ACTION

    override suspend fun execute(parameters: Map<String, Any>): Outcome {
        val question = parameters["question"] as? String
            ?: return Outcome(false, null, "Missing 'question' parameter")

        return try {
            val response = humanInterface(question)
            Outcome(true, response)
        } catch (e: Exception) {
            Outcome(false, null, "Failed to get human response: ${e.message}")
        }
    }

    override fun validateParameters(parameters: Map<String, Any>): Boolean {
        return parameters.containsKey("question") && parameters["question"] is String
    }
}
