@file:Suppress("ClassName")

package link.socket.kore.domain.model.llm

import com.aallam.openai.client.OpenAI as Client
import link.socket.kore.domain.model.tool.ToolDefinition

abstract class AI_Configuration <TD : ToolDefinition, L : LLM<TD>> () {
    abstract val client: Client
    abstract val selectedLLM: L
}

data class StandardAI_Configuration <TD : ToolDefinition, L : LLM<TD>> (
    override val client: Client,
    override val selectedLLM: L,
) : AI_Configuration<TD, L>()
