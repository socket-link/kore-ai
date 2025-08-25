package link.socket.kore.domain.model.llm

import link.socket.kore.domain.model.tool.ToolDefinition


val DEFAULT_AI_CONFIGURATION = AI_Configuration(
    llm = LLM_Gemini._2_5_Flash,
    clientProvider = AI_ClientProvider.Google,
)

data class AI_Configuration <
    TD : ToolDefinition,
    L : LLM<TD>
    > (
    val llm: LLM<TD>,
    val clientProvider: AI_ClientProvider<TD, L>,
)
