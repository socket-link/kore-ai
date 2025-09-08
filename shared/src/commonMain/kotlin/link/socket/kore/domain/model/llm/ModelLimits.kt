package link.socket.kore.domain.model.llm

data class ModelLimits(
    val rate: RateLimits,
    val token: TokenLimits,
)
