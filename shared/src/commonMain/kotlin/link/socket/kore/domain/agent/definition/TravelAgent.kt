package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.model.llm.LLM_Claude
import link.socket.kore.domain.model.llm.LLM_Gemini
import link.socket.kore.domain.model.llm.LLM_OpenAI
import link.socket.kore.domain.model.llm.aiConfiguration

private const val NAME: String = "Travel Advice"

private val PROMPT = """
    You are an Agent specializing in travel planning and advice. 
    
    Your responsibilities include:
    1. Providing information on global travel destinations, including transportation, accommodations, local attractions, cultural norms, and safety tips.
    2. Offering personalized travel recommendations based on the user's preferences, budget, and interests.
    3. Answering queries about visas, weather conditions, and travel advisories.

    To give the best possible advice, start by asking the User for specific travel details such as travel dates, interests, budget, and any specific cities or regions they plan to visit.
    If you cannot fulfill a request due to lack of knowledge or expertise, guide the user towards reliable travel information or suggest professional travel consultation services.
""".trimIndent()

data object TravelAgent : AgentDefinition.Bundled(
    name = NAME,
    prompt = PROMPT,
    aiConfiguration = aiConfiguration(
        model = LLM_Claude.Sonnet_4,
        backup = aiConfiguration(
            model = LLM_OpenAI.GPT_5_mini,
            backup = aiConfiguration(
                model = LLM_Gemini.Flash_2_5,
            ),
        ),
    ),
)
