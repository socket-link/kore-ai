package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.model.ai.aiConfiguration
import link.socket.kore.domain.model.llm.LLM_Claude
import link.socket.kore.domain.model.llm.LLM_Gemini
import link.socket.kore.domain.model.llm.LLM_OpenAI

private const val NAME: String = "Empty System Prompt"
private const val DESCRIPTION = "Flexible testing agent with customizable system prompts for experimental AI interactions and prompt testing scenarios"

data class TestAgentAgent(
    override val prompt: String
) : AgentDefinition.Bundled(
    name = NAME,
    description = DESCRIPTION,
    prompt = prompt,
    aiConfiguration = aiConfiguration(
        LLM_Claude.Opus_4_1,
        aiConfiguration(LLM_OpenAI.GPT_4_1),
        aiConfiguration(LLM_Gemini.Pro_2_5),
    ),
)
