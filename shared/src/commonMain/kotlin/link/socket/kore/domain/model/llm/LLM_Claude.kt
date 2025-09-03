@file:Suppress("ClassName", "ObjectPropertyName", "ObjectPrivatePropertyName")

package link.socket.kore.domain.model.llm

import io.ktor.util.date.*
import link.socket.kore.domain.model.llm.ModelFeatures.Limits
import link.socket.kore.domain.model.llm.ModelFeatures.Limits.TokenLimits
import link.socket.kore.domain.model.llm.ModelFeatures.RelativeReasoning
import link.socket.kore.domain.model.llm.ModelFeatures.RelativeSpeed
import link.socket.kore.domain.model.llm.ModelFeatures.SupportedInputs
import link.socket.kore.domain.model.tool.ProvidedTool
import link.socket.kore.domain.model.tool.Tool_Claude

sealed class LLM_Claude(
    override val name: String,
    override val features: ModelFeatures,
) : LLM<Tool_Claude>(name, features) {

    data object Opus_4_1 : LLM_Claude(
        name = OPUS_4_1_NAME,
        features = Opus_Features,
    )

    data object Opus_4 : LLM_Claude(
        name = OPUS_4_NAME,
        features = Opus_Features,
    )

    data object Sonnet_4 : LLM_Claude(
        name = SONNET_4_NAME,
        features = featuresForSonnet(
            availableTools = _4_Tools,
            limits = Sonnet_4_Limits,
            cutoffDate = Sonnet_4_Cutoff,
        ),
    )

    data object Sonnet_3_7 : LLM_Claude(
        name = SONNET_3_7_NAME,
        features = featuresForSonnet(
            availableTools = _3_7_Tools,
            limits = Sonnet_3_7_Limits,
            cutoffDate = Sonnet_3_7_Cutoff,
        ),
    )

    data object Haiku_3_5 : LLM_Claude(
        name = HAIKU_3_5_NAME,
        features = featuresForHaiku(
            availableTools = Haiku_3_5_Tools,
            limits = Haiku_3_5_Limits,
            cutoffDate = Haiku_3_5_Cutoff,
            supportsPdf = true,
        ),
    )

    data object Haiku_3 : LLM_Claude(
        name = HAIKU_3_NAME,
        features = featuresForHaiku(
            availableTools = Haiku_3_Tools,
            limits = Haiku_3_Limits,
            cutoffDate = Haiku_3_Cutoff,
            supportsPdf = false,
        ),
    )

    companion object Companion {

        // ---- Tools ----

        private val _4_Tools: List<ProvidedTool<Tool_Claude>> = listOf(
            ProvidedTool.Bash(Tool_Claude.Bash),
            ProvidedTool.CodeExecution(Tool_Claude.CodeExecution),
            ProvidedTool.TextEditor(Tool_Claude.TextEditor._4),
            ProvidedTool.WebSearch(Tool_Claude.WebSearch),
        )

        private val _3_7_Tools: List<ProvidedTool<Tool_Claude>> = listOf(
            ProvidedTool.Bash(Tool_Claude.Bash),
            ProvidedTool.CodeExecution(Tool_Claude.CodeExecution),
            ProvidedTool.TextEditor(Tool_Claude.TextEditor._3_7),
            ProvidedTool.WebSearch(Tool_Claude.WebSearch),
        )

        private val Haiku_3_5_Tools: List<ProvidedTool<Tool_Claude>> = listOf(
            ProvidedTool.CodeExecution(Tool_Claude.CodeExecution),
            ProvidedTool.WebSearch(Tool_Claude.WebSearch),
        )

        private val Haiku_3_Tools = emptyList<ProvidedTool<Tool_Claude>>()


        // ---- Rate Limits ----

        private const val TIER_1_RPM = 50
        private const val TIER_2_RPM = 1000
        private const val TIER_3_RPM = 2000
        private const val TIER_4_RPM = 4000

        private val rateLimitsFactory = RateLimitsFactory(
            tier1RequestLimits = Pair(TIER_1_RPM, null),
            tier2RequestLimits = Pair(TIER_2_RPM, null),
            tier3RequestLimits = Pair(TIER_3_RPM, null),
            tier4RequestLimits = Pair(TIER_4_RPM, null),
        )

        private val _4_RateLimits = rateLimitsFactory.createSeparatedRateLimits(
            tier1TPMs = TokenCount._30k to TokenCount._8k,
            tier2TPMs = TokenCount._450k to TokenCount._90k,
            tier3TPMs = TokenCount._800k to TokenCount._160k,
            tier4TPMs = TokenCount._2m to TokenCount._400k,
        )

        private val _3_7_RateLimits = rateLimitsFactory.createSeparatedRateLimits(
            tier1TPMs = TokenCount._20k to TokenCount._8k,
            tier2TPMs = TokenCount._40k to TokenCount._16k,
            tier3TPMs = TokenCount._80k to TokenCount._32k,
            tier4TPMs = TokenCount._200k to TokenCount._80k,
        )

        private val Haiku_RateLimits = rateLimitsFactory.createSeparatedRateLimits(
            tier1TPMs = TokenCount._50k to TokenCount._10k,
            tier2TPMs = TokenCount._100k to TokenCount._20k,
            tier3TPMs = TokenCount._200k to TokenCount._40k,
            tier4TPMs = TokenCount._400k to TokenCount._80k,
        )


        // ---- Token Limits ----

        private val CONTEXT_WINDOW_TOKENS = TokenCount._200k

        private val Opus_TokenLimits = TokenLimits(
            contextWindow = CONTEXT_WINDOW_TOKENS,
            maxOutput = TokenCount._32k,
        )

        private val Sonnet_TokenLimits = TokenLimits(
            contextWindow = CONTEXT_WINDOW_TOKENS,
            maxOutput = TokenCount._64k,
        )

        private val Haiku_3_5_TokenLimits = TokenLimits(
            contextWindow = CONTEXT_WINDOW_TOKENS,
            maxOutput = TokenCount._8192,
        )

        private val Haiku_3_TokenLimits = TokenLimits(
            contextWindow = CONTEXT_WINDOW_TOKENS,
            maxOutput = TokenCount._4096,
        )


        // ---- Limits ----

        private val Opus_Limits = Limits(
            rate = _4_RateLimits,
            token = Opus_TokenLimits,
        )

        private val Sonnet_4_Limits = Limits(
            rate = _4_RateLimits,
            token = Sonnet_TokenLimits,
        )

        private val Sonnet_3_7_Limits = Limits(
            rate = _3_7_RateLimits,
            token = Sonnet_TokenLimits,
        )

        private val Haiku_3_5_Limits = Limits(
            rate = Haiku_RateLimits,
            token = Haiku_3_5_TokenLimits,
        )

        private val Haiku_3_Limits = Limits(
            rate = Haiku_RateLimits,
            token = Haiku_3_TokenLimits,
        )



        // ---- Training Cutoffs ----

        private val Opus_Cutoff = GMTDate(
            year = 2025,
            month = Month.MARCH,
            dayOfMonth = 1,
            hours = 0,
            minutes = 0,
            seconds = 0,
        )

        private val Sonnet_4_Cutoff = Opus_Cutoff

        private val Sonnet_3_7_Cutoff = GMTDate(
            year = 2024,
            month = Month.NOVEMBER,
            dayOfMonth = 1,
            hours = 0,
            minutes = 0,
            seconds = 0,
        )

        private val Haiku_3_5_Cutoff = GMTDate(
            year = 2024,
            month = Month.JULY,
            dayOfMonth = 1,
            hours = 0,
            minutes = 0,
            seconds = 0,
        )

        private val Haiku_3_Cutoff = GMTDate(
            year = 2023,
            month = Month.AUGUST,
            dayOfMonth = 1,
            hours = 0,
            minutes = 0,
            seconds = 0,
        )


        // ---- Model Features ----

        private val Opus_Features = ModelFeatures(
            availableTools = _4_Tools,
            limits = Opus_Limits,
            reasoningLevel = RelativeReasoning.HIGH,
            speed = RelativeSpeed.SLOW,
            supportedInputs = SupportedInputs(
                text = true,
                image = true,
                pdf = true,
            ),
            trainingCutoffDate = Opus_Cutoff,
        )

        private fun featuresForSonnet(
            availableTools: List<ProvidedTool<Tool_Claude>>,
            limits: Limits,
            cutoffDate: GMTDate,
        ): ModelFeatures = ModelFeatures(
            availableTools = availableTools,
            limits = limits,
            reasoningLevel = RelativeReasoning.NORMAL,
            speed = RelativeSpeed.NORMAL,
            supportedInputs = SupportedInputs(
                text = true,
                image = true,
                pdf = true,
            ),
            trainingCutoffDate = cutoffDate,
        )

        private fun featuresForHaiku(
            availableTools: List<ProvidedTool<Tool_Claude>>,
            limits: Limits,
            cutoffDate: GMTDate,
            supportsPdf: Boolean,
        ): ModelFeatures = ModelFeatures(
            availableTools = availableTools,
            limits = limits,
            reasoningLevel = RelativeReasoning.HIGH,
            speed = RelativeSpeed.FAST,
            supportedInputs = SupportedInputs(
                text = true,
                image = true,
                pdf = supportsPdf,
            ),
            trainingCutoffDate = cutoffDate,
        )


        // ---- Model Names ----

        private const val OPUS_4_1_NAME = "claude-opus-4-1"
        private const val OPUS_4_NAME = "claude-opus-4-0"
        private const val SONNET_4_NAME = "claude-sonnet-4-0"
        private const val SONNET_3_7_NAME = "claude-3-7-sonnet-latest"
        private const val HAIKU_3_5_NAME = "claude-3-5-haiku-latest"
        private const val HAIKU_3_NAME = "claude-3-haiku-20240307"


        // ---- Models ----

        val DEFAULT = Sonnet_4

        val ALL_MODELS = listOf(
            Opus_4_1,
            Opus_4,
            Sonnet_4,
            Sonnet_3_7,
            Haiku_3_5,
            Haiku_3,
        )
    }
}
