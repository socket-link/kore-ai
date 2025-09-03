package link.socket.kore.domain.model.llm

import link.socket.kore.domain.model.tool.ToolDefinition


val DEFAULT_AI_CONFIGURATION = aiConfiguration()

data class AI_Configuration <
    TD : ToolDefinition,
    L : LLM<TD>
    > (
    val llm: LLM<TD>,
    val clientProvider: AI_Provider<TD, L>,
)
