package link.socket.kore.domain.model.ai.configuration

import link.socket.kore.domain.model.ai.AI
import link.socket.kore.domain.model.llm.LLM

data class AI_ConfigurationStandard(
    override val aiProvider: AI<*, *>,
    override val selectedLLM: LLM<*>?,
) : AI_Configuration
