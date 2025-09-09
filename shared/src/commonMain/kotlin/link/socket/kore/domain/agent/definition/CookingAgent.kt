package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.ai.aiConfiguration
import link.socket.kore.domain.llm.LLM_Claude
import link.socket.kore.domain.llm.LLM_Gemini
import link.socket.kore.domain.llm.LLM_OpenAI

private const val NAME = "Cooking & Nutrition"
private const val DESCRIPTION = "Culinary expert agent that provides recipe suggestions, cooking tips, ingredient substitutions, and dietary guidance for various cuisines and dietary restrictions"

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
    description = DESCRIPTION,
    prompt = PROMPT,
    aiConfiguration = aiConfiguration(
        LLM_Gemini.Flash_Lite_2_0,
        aiConfiguration(LLM_OpenAI.GPT_4o_mini),
        aiConfiguration(LLM_Claude.Haiku_3_5),
    ),
)
