@file:Suppress("ClassName")

package link.socket.kore.domain.model.ai.configuration

import link.socket.kore.domain.model.ai.AI
import link.socket.kore.domain.model.llm.LLM

interface AI_Configuration {
    val aiProvider: AI<*, *>
    val selectedLLM: LLM<*>?
}
