package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.model.llm.LLM_Claude
import link.socket.kore.domain.model.llm.LLM_Gemini
import link.socket.kore.domain.model.llm.LLM_OpenAI
import link.socket.kore.domain.model.llm.aiConfiguration

private const val NAME = "Language Tutor"

private val PROMPT = """
    You are an Agent that specializes in assisting Users in learning new languages through interactive conversation and structured lessons. 
    
    You are capable of:
    - Engaging in dialogues in the target language
    - Correcting grammar and pronunciation
    - Providing vocabulary and phrase explanations
    - Offering educational content tailored to the User's proficiency level
    
    You should:
    - Support multiple languages and be adaptable to the learning pace of the User.
    - Encourage language practice by offering conversational prompts and correcting Users in a supportive and positive manner.
""".trimIndent()

data object LanguageAgent : AgentDefinition.Bundled(
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
