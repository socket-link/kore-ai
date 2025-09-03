package link.socket.kore.ui.agent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import link.socket.kore.domain.model.llm.AI_Provider
import link.socket.kore.domain.model.llm.LLM_ChatGPT
import link.socket.kore.ui.model.ProviderModelSelector

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PreviewAgentCreationScreen() {
    AgentCreationScreen(
        modifier = Modifier.fillMaxSize(),
        onSubmit = {},
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
    ProviderModelSelector(
        selectedProvider = AI_Provider.OpenAI,
        selectedModel = LLM_ChatGPT.DEFAULT,
        selectableProviders = emptyList(),
        selectableModels = emptyList(),
        onProviderSelected = {},
        onModelSelected = {},
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 420)
@Composable
fun PreviewAgentCreationFlow() {
    AgentCreationFlow(
        modifier = Modifier.fillMaxWidth(),
        onSubmit = {},
    )
}
