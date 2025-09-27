package link.socket.kore.ui.model

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import link.socket.kore.domain.ai.model.AIModel
import link.socket.kore.domain.ai.model.AIModelFeatures

@Composable
fun ModelDetailsSection(
    selectedModel: AIModel,
    modifier: Modifier = Modifier,
) {
    val features: AIModelFeatures = remember(selectedModel) {
        selectedModel.features
    }

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (features.availableTools.isNotEmpty()) {
            ModelToolsSection(features.availableTools)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            ModelFeaturesSection(
                modifier = Modifier.weight(1f),
                features = selectedModel.features,
            )

            ModelLimitsSection(
                modifier = Modifier.weight(1f),
                limits = selectedModel.limits,
                trainingCutoffDate = selectedModel.features.trainingCutoffDate,
            )
        }
    }
}
