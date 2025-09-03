@file:Suppress("ClassName")

package link.socket.kore.domain.model.llm

import io.ktor.util.date.*
import link.socket.kore.domain.model.tool.Tool_OpenAI

sealed class LLM_ChatGPT(
    override val name: String,
    override val features: ModelFeatures,
) : LLM<Tool_OpenAI>(name, features) {

    // TODO: Implement
    data object GPT_5 : LLM_ChatGPT(
        name = "gpt-5",
        features = ModelFeatures(
            availableTools = emptyList(),
            limits = null,
            reasoningLevel = ModelFeatures.RelativeReasoning.HIGH,
            speed = ModelFeatures.RelativeSpeed.NORMAL,
            supportedInputs = ModelFeatures.SupportedInputs.ALL,
            trainingCutoffDate = GMTDate.START,
        ),
    )

//    data object GPT_5_mini : LLM_ChatGPT("")
//    data object GPT_5_nano : LLM_ChatGPT("")
//    data object GPT_4_1 : LLM_ChatGPT("")
//    data object GPT_4_1_mini : LLM_ChatGPT("")
//    data object GPT_4_1_nano : LLM_ChatGPT("")
//    data object GPT_4o : LLM_ChatGPT("")
//    data object GPT_4o_mini : LLM_ChatGPT("")
//    data object o4_mini : LLM_ChatGPT("")
//    data object o3_pro : LLM_ChatGPT("")
//    data object o3 : LLM_ChatGPT("")
//    data object o3_mini : LLM_ChatGPT("")

    companion object {


        // ---- Models ----

        val DEFAULT = GPT_5

        val ALL_MODELS = listOf<LLM_ChatGPT>()
    }
}
