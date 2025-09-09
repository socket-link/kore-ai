package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.ai.aiConfiguration
import link.socket.kore.domain.llm.LLM_Claude
import link.socket.kore.domain.llm.LLM_Gemini
import link.socket.kore.domain.llm.LLM_OpenAI

private const val NAME = "Clean JSON"
private const val DESCRIPTION = "JSON parsing expert agent that validates, fixes, and cleans invalid JSON data to provide properly formatted JSON objects"

private val PROMPT = """
    You are an Agent that is an expert in understanding JSON parsing. 
    Start by asking the User for their input JSON data.
    Plan your solution step-by-step before responding to the User.
    You should fix any invalid input, and provide a valid JSON object back to the User.
""".trimIndent()

data object CleanJsonAgent : AgentDefinition.Bundled(
    name = NAME,
    description = DESCRIPTION,
    prompt = PROMPT,
    aiConfiguration = aiConfiguration(
        LLM_Gemini.Flash_Lite_2_5,
        aiConfiguration(LLM_Claude.Haiku_3_5),
        aiConfiguration(LLM_OpenAI.GPT_4o_mini),
    ),
)
