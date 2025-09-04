package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.agent.AgentInput
import link.socket.kore.domain.model.llm.LLM_Claude
import link.socket.kore.domain.model.llm.LLM_Gemini
import link.socket.kore.domain.model.llm.LLM_OpenAI
import link.socket.kore.domain.model.llm.aiConfiguration

private const val NAME = "Write Code"

private fun promptFrom(technologies: String?): String = """
    You are an Agent that is a programming expert, and you are proficient in the following technologies: $technologies.
    
    Your tasks include:
    - Generate executable code using only the technologies specified in your expertise.
    - Present the generated code to the user for verification and allow them to request any changes.
    - Unless otherwise told, save the final version of the code to the user's local disk, defaulting to the home directory if no path has been specified.

    Note: Before writing the code, plan your solution step-by-step.
    
    Be sure to adhere to any request from the User regarding constraints around the code that you should be creating.
    Example: If a User asks to improve documentation, ensure that the code you generate _only_ includes comments and does not modify the existing code.
""".trimIndent()

data object WriteCodeAgent : AgentDefinition.Bundled(
    name = NAME,
    prompt = promptFrom(technologies = "Kotlin"),
    aiConfiguration = aiConfiguration(
        model = LLM_Claude.Opus_4_1,
        backup = aiConfiguration(
            model = LLM_OpenAI.GPT_4_1,
            backup = aiConfiguration(
                model = LLM_Gemini.Pro_2_5,
            ),
        ),
    ),
) {
    private var technologies: String = "Any language or framework"

    private val technologiesArg =
        AgentInput.ListArg(
            key = "technologyList",
            name = "Technology Name",
            listValue = emptyList(),
        )

    override val prompt: String
        get() = promptFrom(technologies)

    override val neededInputs: List<AgentInput>
        get() = listOf(technologiesArg)
}
