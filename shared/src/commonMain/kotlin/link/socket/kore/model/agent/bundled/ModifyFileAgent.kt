package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import link.socket.kore.model.agent.AgentInput
import link.socket.kore.model.agent.KoreAgent

data class ModifyFileAgent(
    override val openAI: OpenAI,
    override val scope: CoroutineScope,
) : KoreAgent.HumanAndLLMAssisted(scope) {

    private lateinit var filePath: String
    private lateinit var description: String
    private lateinit var technologies: String

    companion object {
        const val NAME = "Modify File"

        private fun instructionsFrom(technologies: String): String =
            "You are an Agent that is an expert programmer in:\n" +
                "$technologies.\n"

        private fun initialPromptFrom(filepath: String, description: String): String {
            // TODO: Get filepath contents
            val fileContents = ""

            return "You are tasked with making user-requested changes to an existing file.\n\n" +
                "Here are the original file contents:\n" +
                "$fileContents\n" +
                "\n\n" +
                "The description of the changes to make is:\n" +
                "$description\n" +
                "\n\n" +
                "Plan your solution step-by-step before making the necessary changes to this file, but do not " +
                    "reveal this plan to the User."
        }

        private val filePathArg = AgentInput.StringArg(
            key = "filePath",
            name = "File Path",
            value = "",
        )

        private val descriptionArg = AgentInput.StringArg(
            key = "codeDescription",
            name = "Code Description",
            value = "",
        )

        private val technologiesArg = AgentInput.ListArg(
            key = "technologyList",
            name = "Technology Name",
            listValue = emptyList(),
        )

        val INPUTS = listOf(filePathArg, descriptionArg, technologiesArg)
    }

    override val name: String = NAME
    override val instructions: String
        get() = "${super.instructions}\n\n" + instructionsFrom(technologies)
    override val initialPrompt: String
        get() = initialPromptFrom(filePath, description)
    override val neededInputs: List<AgentInput> = INPUTS

    override fun parseNeededInputs(inputs: Map<String, AgentInput>) {
        filePath = inputs[filePathArg.key]?.value ?: ""
        description = inputs[descriptionArg.key]?.value ?: ""
        technologies = inputs[technologiesArg.key]?.value ?: ""
    }
}
