package link.socket.kore.domain.limits

import kotlinx.serialization.Serializable

@Serializable
data class ModelLimits(
    val rate: RateLimits,
    val token: TokenLimits,
)
