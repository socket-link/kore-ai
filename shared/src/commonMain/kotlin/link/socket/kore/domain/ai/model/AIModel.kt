@file:Suppress("ClassName")

package link.socket.kore.domain.ai.model

import link.socket.kore.domain.limits.ModelLimits

sealed class AIModel(
    open val name: String,
    open val displayName: String,
    open val description: String,
    open val features: AIModelFeatures,
    open val limits: ModelLimits,
)
