package link.socket.kore.domain.config

import link.socket.kore.domain.ai.AI
import link.socket.kore.domain.llm.LLM

data class AI_ConfigurationStandard(
    override val aiProvider: AI<*, *>,
    override val selectedLLM: LLM<*>?,
) : AI_Configuration
