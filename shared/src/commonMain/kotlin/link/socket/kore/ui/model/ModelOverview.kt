package link.socket.kore.ui.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import link.socket.kore.domain.model.llm.ModelFeatures
import link.socket.kore.domain.model.llm.ModelFeatures.RelativeReasoning
import link.socket.kore.domain.model.llm.ModelFeatures.RelativeSpeed

@Composable
fun ModelOverview(
    features: ModelFeatures,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = "Model Overview",
            style = MaterialTheme.typography.subtitle1,
        )

        // Performance Metrics Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PerformanceChip(
                label = "Reasoning",
                value = features.reasoningLevel.name,
                color = when (features.reasoningLevel) {
                    RelativeReasoning.HIGH -> Color(0xFF4CAF50)
                    RelativeReasoning.NORMAL -> Color(0xFFFF9800)
                    RelativeReasoning.LOW -> Color(0xFFF44336)
                }
            )

            PerformanceChip(
                label = "Speed",
                value = features.speed.name,
                color = when (features.speed) {
                    RelativeSpeed.FAST -> Color(0xFF4CAF50)
                    RelativeSpeed.NORMAL -> Color(0xFFFF9800)
                    RelativeSpeed.SLOW -> Color(0xFFF44336)
                }
            )
        }
    }
}
