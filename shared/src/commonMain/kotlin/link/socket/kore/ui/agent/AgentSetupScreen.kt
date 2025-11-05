package link.socket.kore.ui.agent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import link.socket.kore.domain.agent.KoreAgent
import link.socket.kore.domain.agent.KoreAgentFactory
import link.socket.kore.domain.agent.bundled.AgentDefinition
import link.socket.kore.domain.ai.configuration.AIConfiguration
import link.socket.kore.domain.ai.configuration.AIConfigurationFactory
import link.socket.kore.domain.ai.model.AIModel
import link.socket.kore.domain.ai.provider.AIProvider
import link.socket.kore.ui.model.ModelSelectionBottomSheet
import link.socket.kore.ui.widget.header.Header

@Composable
fun AgentSetupScreen(
    selectedConfig: AIConfiguration,
    aiConfigurationFactory: AIConfigurationFactory,
    agentFactory: KoreAgentFactory,
    onAgentCreated: (KoreAgent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    val selectedAgentDefinition: MutableState<AgentDefinition?> = remember { mutableStateOf(null) }
    val selectedProvider: MutableState<AIProvider<*, *>?> = remember { mutableStateOf(null) }
    val selectedModel: MutableState<AIModel?> = remember { mutableStateOf(null) }

    val suggestedModels: List<Pair<AIProvider<*, *>, AIModel>> = remember(selectedAgentDefinition.value, aiConfigurationFactory) {
        val aiConfiguration = selectedAgentDefinition.value
            ?.suggestedAIConfigurationBuilder(aiConfigurationFactory)
            ?: aiConfigurationFactory.getDefaultConfiguration()

        aiConfiguration.getAvailableModels()
    }

    val bottomSheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Expanded,
    )

    BottomSheetScaffold(
        modifier = modifier,
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = 16.dp,
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                ) {
                    Header(
                        title = "Agent Setup",
                        displayBackIcon = true,
                        onBackClicked = onBackClicked,
                        onActionIconClicked = {
                            val agentDefinition = selectedAgentDefinition.value ?: return@Header

                            val selectedAgentInstance = agentFactory.buildAgent(
                                config = selectedConfig,
                                definition = agentDefinition,
                                scope = scope,
                            )

                            onAgentCreated(selectedAgentInstance)
                        },
                        actionIcon = {
                            Image(
                                imageVector = Icons
                                    .Default.Check,
                                contentDescription = "Back",
                            )
                        },
                    )
                }
            }
        },
        sheetContent = {
            ModelSelectionBottomSheet(
                modifier = Modifier
                    .fillMaxWidth(),
                selectedProvider = selectedProvider.value,
                selectedModel = selectedModel.value,
                suggestedModels = suggestedModels,
                onProviderSelected = { provider ->
                    selectedProvider.value = provider
                    selectedModel.value = null
                },
                onModelSelected = { model ->
                    selectedModel.value = model
                },
            )
        },
    ) { paddingValues ->
        AgentCreationSection(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            partiallySelectedAgent = selectedAgentDefinition.value,
            onAgentPartiallySelected = { agentDefinition ->
                selectedAgentDefinition.value = agentDefinition
                scope.launch {
                    bottomSheetState.expand()
                }
            }
        )
    }
}
