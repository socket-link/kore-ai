package link.socket.kore.model.agent.bundled

import com.aallam.openai.client.OpenAI
import link.socket.kore.model.agent.KoreAgent

data class FixJsonAgent(
    override val openAI: OpenAI,
    val invalidJson: String,
) : KoreAgent.HumanAssisted, KoreAgent.LLMAssisted() {

    companion object {
        private const val INSTRUCTIONS =
            "You are a helpful assistant that is an expert in understanding JSON parsing."

        fun initialPromptFrom(
            invalidJson: String,
        ) = "The given string is not a valid JSON:\n" +
            "$invalidJson\n\n" +
            "Plan your solution step-by-step before you fix this and produce a valid JSON."
    }

    override val instructions: String = INSTRUCTIONS
    override val initialPrompt: String = initialPromptFrom(invalidJson)
}