package link.socket.kore.ui.agent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import link.socket.kore.domain.agent.bundled.WriteCodeAgent
import link.socket.kore.domain.ai.AIProvider_OpenAI
import link.socket.kore.domain.ai.model.AIModel_OpenAI
import link.socket.kore.ui.model.ModelSelectionSection

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PreviewAgentCreationScreen() {
    AgentSelectionSection(
        modifier = Modifier.fillMaxSize(),
        selectedAgentDefinition = WriteCodeAgent,
        setAgentPartiallySelected = {},
        onCreateAgent = {},
        onBackClicked = {},
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 400)
@Composable
fun PreviewAgentColumn() {
    AgentColumn(
        modifier = Modifier.fillMaxSize(),
        onAgentSelected = {},
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 220)
@Composable
fun PreviewProviderModelSelector() {
    ModelSelectionSection(
        selectedProvider = AIProvider_OpenAI,
        selectedModel = AIModel_OpenAI.DEFAULT,
        selectableProviders = emptyList(),
        selectableModels = emptyList(),
        suggestedModels = emptyList(),
        onProviderSelected = {},
        onModelSelected = {},
    )
}
