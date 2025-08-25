@file:Suppress("ClassName")

package link.socket.kore.domain.model.llm

import link.socket.kore.domain.model.tool.Tool_OpenAI

sealed class LLM_ChatGPT(
    override val name: String,
    override val features: ModelFeatures,
) : LLM<Tool_OpenAI>(name, features) {
    // TODO: Implement
//    data object GPT_5 : LLM_ChatGPT("")
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
}
