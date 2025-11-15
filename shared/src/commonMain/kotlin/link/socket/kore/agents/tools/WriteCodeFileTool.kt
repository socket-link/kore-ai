package link.socket.kore.agents.tools

import link.socket.kore.agents.core.AutonomyLevel
import link.socket.kore.agents.core.Outcome

expect class WriteCodeFileTool(baseDirectory: String) : Tool {
    override val name: String
    override val description: String
    override val requiredAutonomyLevel: AutonomyLevel
    override suspend fun execute(parameters: Map<String, Any>): Outcome
    override fun validateParameters(parameters: Map<String, Any>): Boolean
}
