@file:Suppress("ClassName")

package link.socket.kore.domain.ai.model

import kotlinx.serialization.Serializable
import link.socket.kore.domain.limits.ModelLimits

@Serializable
sealed class AIModel(
    open val name: String,
    open val displayName: String,
    open val description: String,
    open val features: AIModelFeatures,
    open val limits: ModelLimits,
)
