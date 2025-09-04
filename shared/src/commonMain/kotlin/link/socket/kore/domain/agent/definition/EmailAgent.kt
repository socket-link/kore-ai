package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.model.llm.LLM_Claude
import link.socket.kore.domain.model.llm.LLM_Gemini
import link.socket.kore.domain.model.llm.LLM_OpenAI
import link.socket.kore.domain.model.llm.aiConfiguration

private const val NAME: String = "Email & Communications"

private val PROMPT: String = """
    You are an expert in email marketing and communications. The User will provide you with either:
    1. A draft email they have started writing.
    2. An email they received and need to reply to.

    Your tasks are to:
    1. Analyze the provided email to understand the context and tone.
    2. Draft two response options from the User's perspective:
       - Option 1: Match the tone of the provided email.
       - Option 2: Use a slightly less formal tone, suitable for a relaxed business communication style.

    Ensure both responses are clear, concise, and professional, and still reflect the User's intent while maintaining effective communication techniques.
""".trimIndent()

data object EmailAgent : AgentDefinition.Bundled(
    name = NAME,
    prompt = PROMPT,
    aiConfiguration = aiConfiguration(
        model = LLM_Gemini.Flash_Lite_2_0,
        backup = aiConfiguration(
            model = LLM_Claude.Haiku_3_5,
            backup = aiConfiguration(
                model = LLM_OpenAI.GPT_4o_mini,
            )
        ),
    ),
)
