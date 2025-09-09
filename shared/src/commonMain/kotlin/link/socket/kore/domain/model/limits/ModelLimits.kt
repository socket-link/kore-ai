package link.socket.kore.domain.model.limits

data class ModelLimits(
    val rate: RateLimits,
    val token: TokenLimits,
)
