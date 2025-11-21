package link.socket.kore.domain.limits

import kotlinx.serialization.Serializable

@Serializable
sealed interface TokenRate {

    @Serializable
    data class Combined(
        val tokensPerMinute: TokenCount,
    ) : TokenRate

    @Serializable
    data class Separated(
        val inputTokensPerMinute: TokenCount,
        val outputTokensPerMinute: TokenCount,
    ) : TokenRate
}
