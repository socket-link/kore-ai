package link.socket.kore.domain.model.llm

data class TokenLimits(
    val contextWindow: TokenCount,
    val maxOutput: TokenCount,
)
