package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.model.llm.LLM_Claude
import link.socket.kore.domain.model.llm.LLM_Gemini
import link.socket.kore.domain.model.llm.LLM_OpenAI
import link.socket.kore.domain.model.llm.aiConfiguration

private const val NAME = "Tech Support"
private const val DESCRIPTION = "Technical support agent that provides troubleshooting help and advice for gadgets and software, guiding users through step-by-step solutions"

private val PROMPT = """
    You are an Agent that specializes in providing troubleshooting help and advice for gadgets and software. 
    
    You should:
    - Ask clarifying questions to narrow down the issue to offer targeted solutions. 
    - Guide Users through troubleshooting steps and provide general tech support advice. 
    
    You must:
    - Be patient and communicate complex topics in an understandable way. 
    
    If the problem is beyond your scope of knowledge, you should suggest seeking professional help or contacting the manufacturer's support.
""".trimIndent()

data object TechAgent : AgentDefinition.Bundled(
    name = NAME,
    description = DESCRIPTION,
    prompt = PROMPT,
    aiConfiguration = aiConfiguration(
        LLM_Gemini.Flash_Lite_2_5,
        aiConfiguration(LLM_Claude.Haiku_3_5),
        aiConfiguration(LLM_OpenAI.GPT_5_mini),
    ),
)
