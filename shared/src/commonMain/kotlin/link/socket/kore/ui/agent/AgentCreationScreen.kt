package link.socket.kore.ui.agent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import link.socket.kore.domain.agent.definition.AgentDefinition
import link.socket.kore.domain.agent.definition.codeAgents
import link.socket.kore.domain.agent.definition.generalAgents
import link.socket.kore.domain.agent.definition.promptAgents
import link.socket.kore.domain.agent.definition.reasoningAgents
import link.socket.kore.domain.model.llm.AI_ConfigurationWithFallback
import link.socket.kore.domain.model.llm.AI_Provider
import link.socket.kore.domain.model.llm.LLM
import link.socket.kore.ui.model.ModelFeaturesDisplay
import link.socket.kore.ui.model.ModelLimitsDisplay
import link.socket.kore.ui.model.ModelOverview
import link.socket.kore.ui.model.ModelSelector
import link.socket.kore.ui.widget.header.Header

@Composable
fun AgentCreationScreen(
    selectedAgentDefinition: AgentDefinition?,
    setSelectedAgentDefinitionChanged: (AgentDefinition?) -> Unit,
    onCreateAgent: (AgentDefinition) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedProvider by remember {
        mutableStateOf<AI_Provider<*, *>?>(null)
    }

    var selectedModel by remember {
        mutableStateOf<LLM<*>?>(null)
    }

    val selectableProviders = remember {
        AI_Provider.ALL_PROVIDERS
    }

    val selectableModels = remember(selectedProvider) {
        derivedStateOf { selectedProvider?.availableModels }
    }

    // TODO: Add this back
//    val neededInputs by remember(partiallySelectedAgent) {
//        mutableStateOf(partiallySelectedAgent?.neededInputs ?: emptyList())
//    }

    val onAgentSelected: (AgentDefinition) -> Unit = { agentDefinition ->
        if (agentDefinition.neededInputs.isNotEmpty()) {
            setSelectedAgentDefinitionChanged(agentDefinition)
        } else {
            onCreateAgent(agentDefinition)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Surface(
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
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
        ) {
            if (selectedAgentDefinition == null) {
                AgentColumn(
                    onAgentSelected = setSelectedAgentDefinitionChanged,
                )
            } else {
                AgentDetails(
                    agentDefinition = selectedAgentDefinition,
                )
            }

            val suggestedModels = remember(selectedAgentDefinition) {
                // TODO: Improve syntax
                (selectedAgentDefinition?.aiConfiguration as? AI_ConfigurationWithFallback)?.getSuggestedModels()
            }

            if (selectedModel != null) {
                selectedModel?.let { model ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ModelSelected(
                            modifier = Modifier.fillMaxWidth(),
                            selectedModel = model,
                            onClearClick = {
                                selectedModel = null
                            },
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            ModelFeaturesDisplay(
                                modifier = Modifier.weight(1f),
                                features = model.features,
                            )

                            ModelLimitsDisplay(
                                modifier = Modifier.weight(1f),
                                limits = model.limits,
                                trainingCutoffDate = model.features.trainingCutoffDate,
                            )
                        }
                    }
                }
            } else {
                ModelSelector(
                    modifier = Modifier.fillMaxWidth(),
                    selectedProvider = selectedProvider,
                    selectedModel = selectedModel,
                    selectableProviders = selectableProviders,
                    selectableModels = selectableModels.value,
                    suggestedModels = suggestedModels,
                    onProviderSelected = { provider ->
                        selectedProvider = provider
                        selectedModel = provider.defaultModel
                    },
                    onModelSelected = { model ->
                        selectedModel = model
                    }
                )
            }
        }
    }
}

@Composable
private fun ModelSelected(
    selectedModel: LLM<*>,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            style = MaterialTheme.typography.subtitle2,
            text = selectedModel.displayName,
        )

        ModelOverview(features = selectedModel.features)

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

// TODO: Use this section to pass inputs to Agent
//if (neededInputs.isNotEmpty()) {
//    // TODO: Remove `!!`
//    AgentInputs(
//        partiallySelectedAgent = partiallySelectedAgent!!,
//        neededInputs = neededInputs,
//        optionalInputs = partiallySelectedAgent!!.optionalInputs,
//        onAgentSubmission = { agentDefinition ->
//            onSubmit(agentDefinition)
//        },
//    )
//}

@Composable
fun AgentColumn(
    modifier: Modifier = Modifier,
    onAgentSelected: (AgentDefinition) -> Unit,
) {
    val agents =
        listOf(
            "Code Agents" to codeAgents,
            "General Agents" to generalAgents,
            "Prompt Agents" to promptAgents,
            "Reasoning Agents" to reasoningAgents,
        )

    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(agents) { (header, row) ->
            AgentRow(header, row, onAgentSelected)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AgentRow(
    category: String,
    agents: List<AgentDefinition>,
    onAgentSelected: (AgentDefinition) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(true) }
    
    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            onClick = { isExpanded = !isExpanded }
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "$category ${if (isExpanded) "▼" else "▶"}",
            )
        }

        if (isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                agents.forEach { agent ->
                    AgentCard(
                        agent = agent,
                        onAgentSelected = { newAgent ->
                            onAgentSelected(newAgent)
                            isExpanded = false
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AgentCard(
    agent: AgentDefinition,
    onAgentSelected: (AgentDefinition) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = 4.dp,
        onClick = {
            onAgentSelected(agent)
        },
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = agent.name,
            textAlign = TextAlign.Start,
        )
    }
}
