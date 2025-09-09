@file:Suppress("ClassName")

package link.socket.kore.domain.ai.configuration

import link.socket.kore.domain.ai.AI
import link.socket.kore.domain.llm.LLM

interface AI_Configuration {
    val aiProvider: AI<*, *>
    val selectedLLM: LLM<*>?
}
