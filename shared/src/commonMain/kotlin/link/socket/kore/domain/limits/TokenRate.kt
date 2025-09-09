package link.socket.kore.domain.limits

sealed interface TokenRate {
    data class Combined(
        val tokensPerMinute: TokenCount,
    ) : TokenRate

    data class Separated(
        val inputTokensPerMinute: TokenCount,
        val outputTokensPerMinute: TokenCount,
    ) : TokenRate
}
