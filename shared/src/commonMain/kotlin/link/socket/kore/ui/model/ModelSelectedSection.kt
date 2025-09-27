package link.socket.kore.ui.model

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.domain.ai.model.AIModel
import link.socket.kore.domain.ai.provider.AIProvider

@Composable
fun ModelSelectedSection(
    selectedProvider: AIProvider<*, *>,
    selectedModel: AIModel,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            style = MaterialTheme.typography.h5,
            text = selectedProvider.name,
        )

        Text(
            style = MaterialTheme.typography.subtitle2,
            text = selectedModel.displayName,
        )

        ModelOverviewSection(features = selectedModel.features)

        OutlinedButton(
            modifier = Modifier
                .requiredHeight(48.dp)
                .fillMaxWidth(),
            onClick = onClearClick,
        ) {
            Text(text = "Clear")
        }
    }
}
