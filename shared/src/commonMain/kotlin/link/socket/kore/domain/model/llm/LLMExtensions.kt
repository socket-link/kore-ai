package link.socket.kore.domain.model.llm

import com.aallam.openai.api.model.ModelId

fun LLM<*>.toModelId(): ModelId =
    ModelId(name)
