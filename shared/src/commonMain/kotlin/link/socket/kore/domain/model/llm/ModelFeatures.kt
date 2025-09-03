package link.socket.kore.domain.model.llm

import io.ktor.util.date.*
import link.socket.kore.domain.model.tool.ProvidedTool

data class ModelFeatures(
    val availableTools: List<ProvidedTool<*>>,
    val limits: Limits?,
    val reasoningLevel: RelativeReasoning,
    val speed: RelativeSpeed,
    val supportedInputs: SupportedInputs,
    val trainingCutoffDate: GMTDate,
) {
    data class Limits(
        val rate: RateLimits,
        val token: TokenLimits,
    ) {
        data class RateLimits(
            val tierFree: Tier? = null,
            val tier1: Tier? = null,
            val tier2: Tier? = null,
            val tier3: Tier? = null,
            val tier4: Tier? = null,
            val tier5: Tier? = null,
        ) {
            data class Tier(
                val requestsPerMinute: Int,
                val requestsPerDay: Int?,
                val tokenRate: TokenRate,
            )
        }

        data class TokenLimits(
            val contextWindow: TokenCount,
            val maxOutput: TokenCount,
        )
    }

    enum class RelativeReasoning {
        LOW, NORMAL, HIGH
    }

    enum class RelativeSpeed {
        SLOW, NORMAL, FAST
    }

    data class SupportedInputs(
        val audio: Boolean = false,
        val image: Boolean = false,
        val pdf: Boolean = false,
        val text: Boolean = false,
        val video: Boolean = false,
    ) {
        companion object {
            val ALL = SupportedInputs(
                audio = true,
                image = true,
                pdf = true,
                text = true,
                video = true,
            )
        }
    }
}
