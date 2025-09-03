@file:Suppress("ClassName", "ObjectPropertyName", "ObjectPrivatePropertyName", "ConstPropertyName")

package link.socket.kore.domain.model.llm

import io.ktor.util.date.*
import link.socket.kore.domain.model.llm.ModelFeatures.Limits
import link.socket.kore.domain.model.llm.ModelFeatures.Limits.TokenLimits
import link.socket.kore.domain.model.llm.ModelFeatures.RelativeReasoning
import link.socket.kore.domain.model.llm.ModelFeatures.RelativeSpeed
import link.socket.kore.domain.model.llm.ModelFeatures.SupportedInputs
import link.socket.kore.domain.model.tool.ProvidedTool
import link.socket.kore.domain.model.tool.Tool_Gemini

sealed class LLM_Gemini(
    override val name: String,
    override val features: ModelFeatures,
) : LLM<Tool_Gemini>(name, features) {

    data object _2_5_Pro : LLM_Gemini(
        name = _2_5_PRO_NAME,
        features = _2_5_Pro_Features,
    )
    data object _2_5_Flash : LLM_Gemini(
        name = _2_5_FLASH_NAME,
        features = _2_5_Flash_Features,
    )
    data object _2_5_Flash_Lite : LLM_Gemini(
        name = _2_5_FLASH_LITE_NAME,
        features = _2_5_Flash_Lite_Features,
    )
    data object _2_0_Flash : LLM_Gemini(
        name = _2_0_FLASH_NAME,
        features = _2_0_Flash_Features,
    )
    data object _2_0_Flash_Lite : LLM_Gemini(
        name = _2_0_FLASH_LITE_NAME,
        features = _2_0_Flash_Lite_Features,
    )

    companion object Companion {

        // ---- Tools ----

        private val _2_5_Tools = listOf(
            ProvidedTool.CodeExecution(Tool_Gemini.CodeExecution),
            ProvidedTool.UrlContext(Tool_Gemini.UrlContext),
            ProvidedTool.WebSearch(Tool_Gemini.WebSearch),
        )

        private val _2_0_Flash_Tools = listOf(
            ProvidedTool.CodeExecution(Tool_Gemini.CodeExecution),
            ProvidedTool.WebSearch(Tool_Gemini.WebSearch),
        )

        private val _2_0_Flash_Lite_Tools = emptyList<ProvidedTool<Tool_Gemini>>()


        // ---- Rate Limits ----

        private const val TIER_FREE_2_5_PRO_RPM = 5
        private const val TIER_FREE_2_5_PRO_RPD = 100
        private const val TIER_1_2_5_PRO_RPM = 150
        private const val TIER_1_2_5_PRO_RPD = 10000
        private const val TIER_2_2_5_PRO_RPM = 1000
        private const val TIER_2_2_5_PRO_RPD = 50000
        private const val TIER_3_2_5_PRO_RPM = 2000
        private val TIER_3_2_5_PRO_RPD = null

        private const val TIER_FREE_2_5_FLASH_RPM = 10
        private const val TIER_FREE_2_5_FLASH_RPD = 250
        private const val TIER_1_2_5_FLASH_RPM = 1000
        private const val TIER_1_2_5_FLASH_RPD = 10000
        private const val TIER_2_2_5_FLASH_RPM = 2000
        private const val TIER_2_2_5_FLASH_RPD = 100000
        private const val TIER_3_2_5_FLASH_RPM = 10000
        private val TIER_3_2_5_FLASH_RPD = null

        private const val TIER_FREE_2_5_FLASH_LITE_RPM = 15
        private const val TIER_FREE_2_5_FLASH_LITE_RPD = 1000
        private const val TIER_1_2_5_FLASH_LITE_RPM = 4000
        private val TIER_1_2_5_FLASH_LITE_RPD = null
        private const val TIER_2_2_5_FLASH_LITE_RPM = 10000
        private val TIER_2_2_5_FLASH_LITE_RPD = null
        private const val TIER_3_2_5_FLASH_LITE_RPM = 30000
        private val TIER_3_2_5_FLASH_LITE_RPD = null

        private const val TIER_FREE_2_0_FLASH_RPM = 15
        private const val TIER_FREE_2_0_FLASH_RPD = 200
        private const val TIER_1_2_0_FLASH_RPM = 2000
        private val TIER_1_2_0_FLASH_RPD = null
        private const val TIER_2_2_0_FLASH_RPM = 10000
        private val TIER_2_2_0_FLASH_RPD = null
        private const val TIER_3_2_0_FLASH_RPM = 30000
        private val TIER_3_2_0_FLASH_RPD = null

        private const val TIER_FREE_2_0_FLASH_LITE_RPM = 30
        private const val TIER_FREE_2_0_FLASH_LITE_RPD = 200
        private const val TIER_1_2_0_FLASH_LITE_RPM = 4000
        private val TIER_1_2_0_FLASH_LITE_RPD = null
        private const val TIER_2_2_0_FLASH_LITE_RPM = 20000
        private val TIER_2_2_0_FLASH_LITE_RPD = null
        private const val TIER_3_2_0_FLASH_LITE_RPM = 30000
        private val TIER_3_2_0_FLASH_LITE_RPD = null

        private val _2_5_Pro_RateLimitsFactory = RateLimitsFactory(
            tierFreeRequestLimits = Pair(TIER_FREE_2_5_PRO_RPM, TIER_FREE_2_5_PRO_RPD),
            tier1RequestLimits = Pair(TIER_1_2_5_PRO_RPM, TIER_1_2_5_PRO_RPD),
            tier2RequestLimits = Pair(TIER_2_2_5_PRO_RPM, TIER_2_2_5_PRO_RPD),
            tier3RequestLimits = Pair(TIER_3_2_5_PRO_RPM, TIER_3_2_5_PRO_RPD),
        )

        private val _2_5_Flash_RateLimitsFactory = RateLimitsFactory(
            tierFreeRequestLimits = Pair(TIER_FREE_2_5_FLASH_RPM, TIER_FREE_2_5_FLASH_RPD),
            tier1RequestLimits = Pair(TIER_1_2_5_FLASH_RPM, TIER_1_2_5_FLASH_RPD),
            tier2RequestLimits = Pair(TIER_2_2_5_FLASH_RPM, TIER_2_2_5_FLASH_RPD),
            tier3RequestLimits = Pair(TIER_3_2_5_FLASH_RPM, TIER_3_2_5_FLASH_RPD),
        )

        private val _2_5_Flash_Lite_RateLimitsFactory = RateLimitsFactory(
            tierFreeRequestLimits = Pair(TIER_FREE_2_5_FLASH_LITE_RPM, TIER_FREE_2_5_FLASH_LITE_RPD),
            tier1RequestLimits = Pair(TIER_1_2_5_FLASH_LITE_RPM, TIER_1_2_5_FLASH_LITE_RPD),
            tier2RequestLimits = Pair(TIER_2_2_5_FLASH_LITE_RPM, TIER_2_2_5_FLASH_LITE_RPD),
            tier3RequestLimits = Pair(TIER_3_2_5_FLASH_LITE_RPM, TIER_3_2_5_FLASH_LITE_RPD),
        )

        private val _2_0_Flash_RateLimitsFactory = RateLimitsFactory(
            tierFreeRequestLimits = Pair(TIER_FREE_2_0_FLASH_RPM, TIER_FREE_2_0_FLASH_RPD),
            tier1RequestLimits = Pair(TIER_1_2_0_FLASH_RPM, TIER_1_2_0_FLASH_RPD),
            tier2RequestLimits = Pair(TIER_2_2_0_FLASH_RPM, TIER_2_2_0_FLASH_RPD),
            tier3RequestLimits = Pair(TIER_3_2_0_FLASH_RPM, TIER_3_2_0_FLASH_RPD),
        )

        private val _2_0_Flash_Lite_RateLimitsFactory = RateLimitsFactory(
            tierFreeRequestLimits = Pair(TIER_FREE_2_0_FLASH_LITE_RPM, TIER_FREE_2_0_FLASH_LITE_RPD),
            tier1RequestLimits = Pair(TIER_1_2_0_FLASH_LITE_RPM, TIER_1_2_0_FLASH_LITE_RPD),
            tier2RequestLimits = Pair(TIER_2_2_0_FLASH_LITE_RPM, TIER_2_2_0_FLASH_LITE_RPD),
            tier3RequestLimits = Pair(TIER_3_2_0_FLASH_LITE_RPM, TIER_3_2_0_FLASH_LITE_RPD),
        )

        private val _2_5_Pro_RateLimits = _2_5_Pro_RateLimitsFactory.createRateLimits(
            tierFreeTPM = TokenCount._250k,
            tier1TPM = TokenCount._2m,
            tier2TPM = TokenCount._5m,
            tier3TPM = TokenCount._8m,
        )

        private val _2_5_Flash_RateLimits = _2_5_Flash_RateLimitsFactory.createRateLimits(
            tierFreeTPM = TokenCount._250k,
            tier1TPM = TokenCount._1m,
            tier2TPM = TokenCount._3m,
            tier3TPM = TokenCount._8m,
        )

        private val _2_5_Flash_Lite_RateLimits = _2_5_Flash_Lite_RateLimitsFactory.createRateLimits(
            tierFreeTPM = TokenCount._250k,
            tier1TPM = TokenCount._4m,
            tier2TPM = TokenCount._10m,
            tier3TPM = TokenCount._30m,
        )

        private val _2_0_Flash_RateLimits = _2_0_Flash_RateLimitsFactory.createRateLimits(
            tierFreeTPM = TokenCount._1m,
            tier1TPM = TokenCount._4m,
            tier2TPM = TokenCount._10m,
            tier3TPM = TokenCount._30m,
        )

        private val _2_0_Flash_Lite_RateLimits = _2_0_Flash_Lite_RateLimitsFactory.createRateLimits(
            tierFreeTPM = TokenCount._1m,
            tier1TPM = TokenCount._4m,
            tier2TPM = TokenCount._10m,
            tier3TPM = TokenCount._30m,
        )


        // ---- Token Limits ----

        private val CONTEXT_WINDOW_TOKENS = TokenCount._1m

        private val _2_5_TokenLimits = TokenLimits(
            contextWindow = CONTEXT_WINDOW_TOKENS,
            maxOutput = TokenCount._64k,
        )

        private val _2_0_TokenLimits = TokenLimits(
            contextWindow = CONTEXT_WINDOW_TOKENS,
            maxOutput = TokenCount._8192,
        )


        // ---- Limits ----

        private val _2_5_Pro_Limits = Limits(
            rate = _2_5_Pro_RateLimits,
            token = _2_5_TokenLimits,
        )

        private val _2_5_Flash_Limits = Limits(
            rate = _2_5_Flash_RateLimits,
            token = _2_5_TokenLimits,
        )

        private val _2_5_Flash_Lite_Limits = Limits(
            rate = _2_5_Flash_Lite_RateLimits,
            token = _2_5_TokenLimits,
        )

        private val _2_0_Flash_Limits = Limits(
            rate = _2_0_Flash_RateLimits,
            token = _2_0_TokenLimits,
        )

        private val _2_0_Flash_Lite_Limits = Limits(
            rate = _2_0_Flash_Lite_RateLimits,
            token = _2_0_TokenLimits,
        )


        // ---- Training Cutoffs ----

        private val _2_5_Cutoff = GMTDate(
            year = 2025,
            month = Month.JANUARY,
            dayOfMonth = 1,
            hours = 0,
            minutes = 0,
            seconds = 0,
        )

        private val _2_0_Cutoff = GMTDate(
            year = 2024,
            month = Month.AUGUST,
            dayOfMonth = 1,
            hours = 0,
            minutes = 0,
            seconds = 0,
        )


        // ---- Model Features ----

        private val _2_5_Pro_Features = ModelFeatures(
            availableTools = _2_5_Tools,
            limits = _2_5_Pro_Limits,
            reasoningLevel = RelativeReasoning.HIGH,
            speed = RelativeSpeed.SLOW,
            supportedInputs = SupportedInputs.ALL,
            trainingCutoffDate = _2_5_Cutoff,
        )

        private val _2_5_Flash_Features = ModelFeatures(
            availableTools = _2_5_Tools,
            limits = _2_5_Flash_Limits,
            reasoningLevel = RelativeReasoning.NORMAL,
            speed = RelativeSpeed.NORMAL,
            supportedInputs = SupportedInputs(
                audio = true,
                image = true,
                text = true,
                video = true,
            ),
            trainingCutoffDate = _2_5_Cutoff,
        )

        private val _2_5_Flash_Lite_Features = ModelFeatures(
            availableTools = _2_5_Tools,
            limits = _2_5_Flash_Lite_Limits,
            reasoningLevel = RelativeReasoning.NORMAL,
            speed = RelativeSpeed.FAST,
            supportedInputs = SupportedInputs.ALL,
            trainingCutoffDate = _2_5_Cutoff,
        )

        private val _2_0_Flash_Features = ModelFeatures(
            availableTools = _2_0_Flash_Tools,
            limits = _2_0_Flash_Limits,
            reasoningLevel = RelativeReasoning.NORMAL,
            speed = RelativeSpeed.FAST,
            supportedInputs = SupportedInputs(
                audio = true,
                image = true,
                text = true,
                video = true,
            ),
            trainingCutoffDate = _2_0_Cutoff,
        )

        private val _2_0_Flash_Lite_Features = ModelFeatures(
            availableTools =_2_0_Flash_Lite_Tools,
            limits = _2_0_Flash_Lite_Limits,
            reasoningLevel = RelativeReasoning.LOW,
            speed = RelativeSpeed.FAST,
            supportedInputs = SupportedInputs(
                audio = true,
                image = true,
                text = true,
                video = true,
            ),
            trainingCutoffDate = _2_0_Cutoff,
        )


        // ---- Model Names ----

        private const val _2_5_PRO_NAME = "gemini-2.5-pro"
        private const val _2_5_FLASH_NAME = "gemini-2.5-flash"
        private const val _2_5_FLASH_LITE_NAME = "gemini-2.5-flash-lite"
        private const val _2_0_FLASH_NAME = "gemini-2.0-flash"
        private const val _2_0_FLASH_LITE_NAME = "gemini-2.0-flash-lite"


        // ---- Models ----

        val DEFAULT = _2_5_Flash

        val ALL_MODELS = listOf(
            _2_5_Pro,
            _2_5_Flash,
            _2_5_Flash_Lite,
            _2_0_Flash,
            _2_0_Flash_Lite,
        )
    }
}
