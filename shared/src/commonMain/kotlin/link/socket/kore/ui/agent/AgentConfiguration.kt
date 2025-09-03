package link.socket.kore.ui.agent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import link.socket.kore.domain.model.llm.AI_Provider
import link.socket.kore.domain.model.llm.LLM
import link.socket.kore.ui.model.ModelFeaturesDisplay
import link.socket.kore.ui.model.ProviderModelSelector

@Composable
fun AgentConfiguration(
    selectedProvider: AI_Provider<*, *>?,
    selectedModel: LLM<*>?,
    selectableProviders: List<AI_Provider<*, *>>,
    selectableModels: List<LLM<*>>?,
    onProviderSelected: (AI_Provider<*, *>) -> Unit,
    onModelSelected: (LLM<*>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        ProviderModelSelector(
            modifier = Modifier.fillMaxWidth(),
            selectedProvider = selectedProvider,
            selectedModel = selectedModel,
            selectableProviders = selectableProviders,
            selectableModels = selectableModels,
            onProviderSelected = onProviderSelected,
            onModelSelected = onModelSelected,
        )

        // Display model features when a model is selected
        selectedModel?.let { model ->
            ModelFeaturesDisplay(
                modifier = Modifier.fillMaxWidth(),
                features = model.features,
            )
        }
    }
}
