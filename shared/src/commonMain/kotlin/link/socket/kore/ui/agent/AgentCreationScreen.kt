package link.socket.kore.ui.agent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.domain.agent.KoreAgent
import link.socket.kore.domain.agent.KoreAgentFactory
import link.socket.kore.domain.agent.bundled.AgentDefinition
import link.socket.kore.domain.ai.configuration.AIConfiguration
import link.socket.kore.domain.ai.configuration.AIConfigurationFactory
import link.socket.kore.domain.ai.model.AIModel
import link.socket.kore.domain.ai.provider.AIProvider
import link.socket.kore.ui.model.ModelDetailsSection
import link.socket.kore.ui.model.ModelSelectionScreen
import link.socket.kore.ui.widget.header.Header

@Composable
fun AgentCreationScreen(
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

    val suggestedModels: State<List<Pair<AIProvider<*, *>, AIModel>>?> = remember(selectedAgentDefinition, aiConfigurationFactory) {
        val aiConfiguration = selectedAgentDefinition.value?.defaultAIConfigurationBuilder(aiConfigurationFactory)

        derivedStateOf {
            aiConfiguration?.getAvailableModels()
        }
    }

    Scaffold(
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
                        actionIcon = @Composable {
                            Image(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Back",
                            )
                        },
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues),
        ) {
            selectedAgentDefinition.value?.let { definition ->
                AgentDetailsSection(
                    modifier = Modifier.fillMaxWidth(),
                    agentDefinition = definition,
                )
            } ?: run {
                AgentSelectionSection(
                    modifier = Modifier.fillMaxWidth(),
                    partiallySelectedAgent = selectedAgentDefinition.value,
                    setAgentPartiallySelected = { agent ->
                        selectedAgentDefinition.value = agent
                    },
                    onCreateAgent = { agentDefinition ->
                        selectedAgentDefinition.value = agentDefinition
                    },
                )
            }

            selectedModel.value?.let { model ->
                ModelDetailsSection(
                    selectedModel = model,
                )
            } ?: run {
                ModelSelectionScreen(
                    modifier = Modifier.fillMaxWidth(),
                    selectedProvider = selectedProvider.value,
                    selectedModel = selectedModel.value,
                    suggestedModels = suggestedModels.value,
                    onProviderSelected = { provider ->
                        selectedProvider.value = provider
                        selectedModel.value = null
                    },
                    onModelSelected = { model ->
                        selectedModel.value = model
                    },
                )
            }
        }
    }
}
