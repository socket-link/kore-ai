package link.socket.kore.domain.agent.bundled.prompt

import link.socket.kore.domain.agent.AgentDefinition

class TestAgentAgent(override val prompt: String) : AgentDefinition() {

    override val name: String = "Empty System Prompt"
}
