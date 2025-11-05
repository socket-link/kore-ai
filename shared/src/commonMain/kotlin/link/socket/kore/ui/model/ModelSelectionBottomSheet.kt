package link.socket.kore.ui.model

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import link.socket.kore.domain.ai.model.AIModel
import link.socket.kore.domain.ai.provider.AIProvider

@Composable
fun ModelSelectionBottomSheet(
    selectedProvider: AIProvider<*, *>?,
    selectedModel: AIModel?,
    suggestedModels: List<Pair<AIProvider<*, *>, AIModel>>?,
    onProviderSelected: (AIProvider<*, *>?) -> Unit,
    onModelSelected: (AIModel?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectableModels: List<AIModel>? = remember(selectedProvider) {
        derivedStateOf { selectedProvider?.availableModels }.value
    }

    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        if (selectedProvider != null && selectedModel != null) {
            ModelSelectedSection(
                modifier = Modifier
                    .fillMaxWidth(),
                selectedProvider = selectedProvider,
                selectedModel = selectedModel,
                onClearClick = {
                    onProviderSelected(null)
                    onModelSelected(null)
                },
            )
        } else {
            ModelSelectionSection(
                modifier = Modifier
                    .fillMaxWidth(),
                selectedProvider = selectedProvider,
                selectedModel = selectedModel,
                selectableProviders = AIProvider.ALL_PROVIDERS,
                selectableModels = selectableModels,
                suggestedModels = suggestedModels,
                onProviderSelected = onProviderSelected,
                onModelSelected = onModelSelected,
            )
        }
    }
}
