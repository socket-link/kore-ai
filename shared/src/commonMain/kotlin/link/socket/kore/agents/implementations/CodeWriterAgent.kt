package link.socket.kore.agents.implementations

import link.socket.kore.agents.core.Context
import link.socket.kore.agents.core.Message
import link.socket.kore.agents.core.MessageSeverity
import link.socket.kore.agents.core.MinimalAutonomousAgent
import link.socket.kore.agents.core.Outcome
import link.socket.kore.agents.core.Plan
import link.socket.kore.agents.tools.Tool

/**
 * First concrete agent that can read tickets, generate code, and validate results (scaffold).
 *
 * This implementation intentionally keeps logic simple and deterministic to
 * satisfy the initial milestone requirements. It is multiplatform-friendly as
 * it only relies on commonMain types and contracts.
 */
class CodeWriterAgent(
    private val tools: Map<String, Tool>
) : MinimalAutonomousAgent {
    private var currentTask: String? = null
    private var currentPlan: Plan? = null
    private var executionHistory: MutableList<String> = mutableListOf()

    fun setTask(task: String) {
        currentTask = task
        currentPlan = null
        executionHistory.clear()
    }

    override fun perceive(): Context {
        return Context(
            currentState = mapOf(
                "task" to (currentTask ?: "none"),
                "planExists" to (currentPlan != null),
                "executionHistory" to executionHistory.toList()
            )
        )
    }

    override fun reason(): Plan {
        val task = currentTask ?: return Plan(
            steps = emptyList(),
            estimatedComplexity = 0,
            requiresHumanApproval = true
        )

        return if (task.contains("Add a new tool called")) {
            Plan(
                steps = listOf(
                    "Read existing tool implementations",
                    "Generate new tool code",
                    "Write tool to file",
                    "Ask human for validation"
                ),
                estimatedComplexity = 2,
                requiresHumanApproval = true
            )
        } else {
            Plan(
                steps = listOf("Ask human for clarification"),
                estimatedComplexity = 1,
                requiresHumanApproval = true
            )
        }
    }

    override fun plan(): Plan {
        currentPlan = reason()
        return currentPlan!!
    }

    override fun act(): Outcome {
        val plan = currentPlan ?: return Outcome(
            success = false,
            result = null,
            errorMessage = "No plan exists"
        )

        val nextStep = plan.steps.getOrNull(executionHistory.size)
            ?: return Outcome(success = true, result = "Plan completed")

        executionHistory.add(nextStep)

        // Placeholder for actual execution logic. Future versions may select
        // and invoke concrete tools from [tools] based on the step.
        return Outcome(
            success = true,
            result = "Executed: $nextStep"
        )
    }

    override fun signal(): Message? {
        return if (currentPlan?.requiresHumanApproval == true) {
            Message(
                content = "Plan requires approval: ${currentPlan?.steps}",
                severity = MessageSeverity.QUESTION,
                requiresResponse = true
            )
        } else null
    }
}
