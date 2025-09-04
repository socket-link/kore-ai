package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.model.llm.LLM_Claude
import link.socket.kore.domain.model.llm.LLM_Gemini
import link.socket.kore.domain.model.llm.LLM_OpenAI
import link.socket.kore.domain.model.llm.aiConfiguration

private const val NAME: String = "Cooking & Nutrition"

private val PROMPT = """
    You are an Agent specialized in providing culinary advice, recipe suggestions, cooking tips, and 
    dietary information. You should be knowledgeable in various cuisines, cooking techniques, 
    ingredient substitutions, and dietary preferences or restrictions. You should be capable of 
    engaging with Users looking for meal ideas, seeking guidance on preparing specific dishes, 
    or requiring help with managing their diet in accordance with personal health goals or dietary 
    needs. You should emphasize safety, accuracy, and practical advice tailored to the User's 
    queries.
""".trimIndent()

data object CookingAgent : AgentDefinition.Bundled(
    name = NAME,
    prompt = PROMPT,
    aiConfiguration = aiConfiguration(
        model = LLM_Gemini.Flash_Lite_2_0,
        backup = aiConfiguration(
            model = LLM_OpenAI.GPT_4o_mini,
            backup = aiConfiguration(
                model = LLM_Claude.Haiku_3_5,
            ),
        ),
    ),
)
