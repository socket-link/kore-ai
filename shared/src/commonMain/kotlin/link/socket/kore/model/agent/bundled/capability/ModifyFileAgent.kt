package link.socket.kore.model.agent.bundled.capability

import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.model.agent.AgentInput

data object ModifyFileAgent : AgentDefinition() {

    private lateinit var filePath: String
    private lateinit var technologies: String

    private val filePathArg = AgentInput.StringArg(
        key = "filePath",
        name = "File Path",
        value = "",
    )

    private val technologiesArg = AgentInput.ListArg(
        key = "technologyList",
        name = "Technology Name",
        listValue = emptyList(),
    )

    override val name: String = "Modify File"

    override val prompt: String
        get() = instructionsFrom(technologies)

    override val neededInputs: List<AgentInput> = listOf(filePathArg, technologiesArg)

    override fun parseInputs(inputs: Map<String, AgentInput>) {
        filePath = inputs[filePathArg.key]?.value ?: ""
        technologies = inputs[technologiesArg.key]?.value ?: ""
    }

    private fun instructionsFrom(technologies: String): String =
        "You are an Agent that is an expert programmer in:\n" +
                "$technologies.\n" +
                "You are tasked with making user-requested changes to an existing file.\n\n" +
                "Plan your solution step-by-step before making the necessary changes to this file, but do not " +
                "reveal this plan to the User."
}
