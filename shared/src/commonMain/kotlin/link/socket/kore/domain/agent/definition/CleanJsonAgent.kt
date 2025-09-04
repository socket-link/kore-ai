package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.model.llm.LLM_Claude
import link.socket.kore.domain.model.llm.LLM_Gemini
import link.socket.kore.domain.model.llm.LLM_OpenAI
import link.socket.kore.domain.model.llm.aiConfiguration

private const val NAME = "Clean JSON"

private val PROMPT = """
    You are an Agent that is an expert in understanding JSON parsing. 
    Start by asking the User for their input JSON data.
    Plan your solution step-by-step before responding to the User.
    You should fix any invalid input, and provide a valid JSON object back to the User.
""".trimIndent()

data object CleanJsonAgent : AgentDefinition.Bundled(
    name = NAME,
    prompt = PROMPT,
    aiConfiguration = aiConfiguration(
        model = LLM_Gemini.Flash_Lite_2_5,
        backup = aiConfiguration(
            model = LLM_Claude.Haiku_3_5,
            backup = aiConfiguration(
                model = LLM_OpenAI.GPT_4o_mini,
            ),
        ),
    ),
)
