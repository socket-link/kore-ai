package link.socket.kore.agents.tools

import link.socket.kore.agents.core.AutonomyLevel
import link.socket.kore.agents.core.Outcome

/**
 * Expect declaration for a tool that executes project tests via Gradle.
 * Implementations should execute tests at the given project root and
 * return the combined output with success based on process exit code.
 */
expect class RunTestsTool(projectRoot: String) : Tool {
    override val name: String
    override val description: String
    override val requiredAutonomyLevel: AutonomyLevel
    override suspend fun execute(parameters: Map<String, Any>): Outcome
    override fun validateParameters(parameters: Map<String, Any>): Boolean
}
