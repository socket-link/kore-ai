package link.socket.kore.domain.model.limits

data class TokenLimits(
    val contextWindow: TokenCount,
    val maxOutput: TokenCount,
)
