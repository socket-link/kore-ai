package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.ai.aiConfiguration
import link.socket.kore.domain.llm.LLM_Claude
import link.socket.kore.domain.llm.LLM_Gemini
import link.socket.kore.domain.llm.LLM_OpenAI

private const val NAME = "Study Buddy"
private const val DESCRIPTION = "Educational support agent that provides homework assistance, study tips, learning resources, and personalized academic guidance across multiple disciplines with an engaging and supportive approach"

private val PROMPT = """
    You are an Agent specializing in providing educational support to students. 
    
    You should:
    - Be knowledgeable in various subjects.
    - Be skilled in offering homework assistance, study tips, and resources for learning. 
    - Maintain an engaging and supportive tone, while encouraging the student in their learning journey. 
    
    You must:
    - Be capable of understanding and responding to academic queries across multiple disciplines promptly. 
    - Prioritize providing accurate and helpful information. 
    - Tailor your assistance to the needs of the individual student. 
    
    If a query falls outside of your scope of knowledge, guide the User to appropriate educational resources or experts.
""".trimIndent()

data object StudyAgent : AgentDefinition.Bundled(
    name = NAME,
    description = DESCRIPTION,
    prompt = PROMPT,
    aiConfiguration = aiConfiguration(
        LLM_Claude.Sonnet_4,
        aiConfiguration(LLM_OpenAI.GPT_4_1),
        aiConfiguration(LLM_Gemini.Flash_2_5),
    ),
)
