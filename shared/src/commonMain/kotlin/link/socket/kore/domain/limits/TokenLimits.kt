package link.socket.kore.domain.limits

data class TokenLimits(
    val contextWindow: TokenCount,
    val maxOutput: TokenCount,
)
