package link.socket.kore.domain.agent.definition

private const val NAME: String = "Empty System Prompt"

data class TestAgentAgent(
    override val prompt: String
) : AgentDefinition.Bundled(NAME, prompt)
