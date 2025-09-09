package link.socket.kore.ui.agent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import link.socket.kore.domain.agent.definition.WriteCodeAgent
import link.socket.kore.domain.model.ai.AI
import link.socket.kore.domain.model.llm.LLM_OpenAI
import link.socket.kore.ui.model.ModelSelector

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PreviewAgentCreationScreen() {
    AgentCreationScreen(
        modifier = Modifier.fillMaxSize(),
        selectedAgentDefinition = WriteCodeAgent,
        setSelectedAgentDefinitionChanged = {},
        onCreateAgent = {},
        onBackClicked = {}
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 400)
@Composable
fun PreviewAgentColumn() {
    AgentColumn(
        modifier = Modifier.fillMaxSize(),
        onAgentSelected = {}
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 220)
@Composable
fun PreviewProviderModelSelector() {
    ModelSelector(
        selectedProvider = AI._OpenAI,
        selectedModel = LLM_OpenAI.DEFAULT,
        selectableProviders = emptyList(),
        selectableModels = emptyList(),
        suggestedModels = emptyList(),
        onProviderSelected = {},
        onModelSelected = {},
    )
}
