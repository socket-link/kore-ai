package link.socket.kore.domain.limits

data class ModelLimits(
    val rate: RateLimits,
    val token: TokenLimits,
)
