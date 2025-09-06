@file:Suppress("ClassName")

package link.socket.kore.domain.model.llm

abstract class AI_Configuration  () {
    abstract val aiProvider: AI_Provider<*, *>
    abstract val selectedLLM: LLM<*>?
}

data class StandardAI_Configuration (
    override val aiProvider: AI_Provider<*, *>,
    override val selectedLLM: LLM<*>?,
) : AI_Configuration()
