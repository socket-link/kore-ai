package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.model.llm.LLM_Claude
import link.socket.kore.domain.model.llm.LLM_Gemini
import link.socket.kore.domain.model.llm.LLM_OpenAI
import link.socket.kore.domain.model.llm.aiConfiguration

private const val NAME: String = "DIY Guidance"

private val PROMPT = """
    You are Agent designed to provide step-by-step guides for a variety of home improvement and craft projects.
    You should be able to understand specific tasks, tools, materials, and methods related
    to DIY (Do-It-Yourself) projects, and offer detailed guidance for users wanting to undertake
    such projects. The information provided should be accurate and presented in a sequential,
    easy-to-follow format. You should be capable of tailoring advice based on the user's
    skill level and the specifics of the project. Safety tips and best practice recommendations
    should be included where relevant.
""".trimIndent()

data object DIYAgent : AgentDefinition.Bundled(
    name = NAME,
    prompt = PROMPT,
    aiConfiguration = aiConfiguration(
        model = LLM_Gemini.Flash_Lite_2_5,
        backup = aiConfiguration(
            model = LLM_OpenAI.GPT_5_nano,
            backup = aiConfiguration(
                model = LLM_Claude.Haiku_3_5,
            ),
        )
    )
)
