package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.model.llm.LLM_Claude
import link.socket.kore.domain.model.llm.LLM_Gemini
import link.socket.kore.domain.model.llm.LLM_OpenAI
import link.socket.kore.domain.model.llm.aiConfiguration

private const val NAME: String = "Empty System Prompt"

data class TestAgentAgent(
    override val prompt: String
) : AgentDefinition.Bundled(
    name = NAME,
    prompt = prompt,
    aiConfiguration = aiConfiguration(
        model = LLM_Claude.Opus_4_1,
        backup = aiConfiguration(
            model = LLM_OpenAI.GPT_4_1,
            backup = aiConfiguration(
                model = LLM_Gemini.Pro_2_5,
            ),
        ),
    ),
)
