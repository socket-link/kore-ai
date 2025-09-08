package link.socket.kore.domain.model.llm

sealed class Tier(
    open val type: TierType,
    open val tokenRate: TokenRate,
    open val requestsPerMinute: Int,
    open val requestsPerDay: Int?,
) {
    data class FreeTier(
        override val tokenRate: TokenRate,
        override val requestsPerMinute: Int,
        override val requestsPerDay: Int? = null,
    ) : Tier(
        type = TierType.FREE,
        tokenRate = tokenRate,
        requestsPerMinute = requestsPerMinute,
        requestsPerDay = requestsPerMinute,
    )

    data class Tier1(
        override val tokenRate: TokenRate,
        override val requestsPerMinute: Int,
        override val requestsPerDay: Int? = null,
    ) : Tier(
        type = TierType.TIER_1,
        tokenRate = tokenRate,
        requestsPerMinute = requestsPerMinute,
        requestsPerDay = requestsPerDay,
    )

    data class Tier2(
        override val tokenRate: TokenRate,
        override val requestsPerMinute: Int,
        override val requestsPerDay: Int? = null,
    ) : Tier(
        type = TierType.TIER_2,
        tokenRate = tokenRate,
        requestsPerMinute = requestsPerMinute,
        requestsPerDay = requestsPerDay,
    )

    data class Tier3(
        override val tokenRate: TokenRate,
        override val requestsPerMinute: Int,
        override val requestsPerDay: Int? = null,
    ) : Tier(
        type = TierType.TIER_3,
        tokenRate = tokenRate,
        requestsPerMinute = requestsPerMinute,
        requestsPerDay = requestsPerDay,
    )

    data class Tier4(
        override val tokenRate: TokenRate,
        override val requestsPerMinute: Int,
        override val requestsPerDay: Int? = null,
    ) : Tier(
        type = TierType.TIER_4,
        tokenRate = tokenRate,
        requestsPerMinute = requestsPerMinute,
        requestsPerDay = requestsPerDay,
    )

    data class Tier5(
        override val tokenRate: TokenRate,
        override val requestsPerMinute: Int,
        override val requestsPerDay: Int? = null,
    ) : Tier(
        type = TierType.TIER_5,
        tokenRate = tokenRate,
        requestsPerMinute = requestsPerMinute,
        requestsPerDay = requestsPerDay,
    )
}
