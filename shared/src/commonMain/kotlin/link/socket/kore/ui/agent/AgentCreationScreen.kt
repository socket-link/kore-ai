package link.socket.kore.ui.agent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import link.socket.kore.domain.agent.definition.AgentDefinition
import link.socket.kore.domain.agent.definition.codeAgents
import link.socket.kore.domain.agent.definition.generalAgents
import link.socket.kore.domain.agent.definition.promptAgents
import link.socket.kore.domain.agent.definition.reasoningAgents
import link.socket.kore.domain.model.llm.AI_Provider
import link.socket.kore.domain.model.llm.LLM
import link.socket.kore.ui.widget.header.Header

@Composable
fun AgentCreationScreen(
    modifier: Modifier = Modifier,
    onSubmit: (AgentDefinition) -> Unit,
    onBackClicked: () -> Unit,
) {
    var partiallySelectedAgent by remember {
        mutableStateOf<AgentDefinition?>(null)
    }

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

    val neededInputs by remember(partiallySelectedAgent) {
        mutableStateOf(partiallySelectedAgent?.neededInputs ?: emptyList())
    }

    val onAgentSelected: (AgentDefinition) -> Unit = { agentDefinition ->
        if (agentDefinition.neededInputs.isNotEmpty()) {
            partiallySelectedAgent = agentDefinition
        } else {
            onSubmit(agentDefinition)
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
                        title = "Agent Configuration",
                        displayBackIcon = true,
                        onBackClicked = onBackClicked,
                    )
                }
            }
        },
    ) { paddingValues ->
        Column {
            AgentConfiguration(
                selectedProvider = selectedProvider,
                selectedModel = selectedModel,
                selectableProviders = selectableProviders,
                selectableModels = selectableModels.value,
                onProviderSelected = { provider ->
                    selectedProvider = provider
                    selectedModel = provider.defaultModel
                },
                onModelSelected = { model ->
                    selectedModel = model
                }
            )

            if (neededInputs.isNotEmpty()) {
                AgentInputs(
                    modifier = Modifier.padding(paddingValues),
                    partiallySelectedAgent = partiallySelectedAgent!!,
                    neededInputs = neededInputs,
                    optionalInputs = partiallySelectedAgent!!.optionalInputs,
                    onAgentSubmission = { agentDefinition ->
                        onSubmit(agentDefinition)
                    },
                )
            }

            AgentColumn(
                modifier = Modifier.padding(paddingValues),
                onAgentSelected = onAgentSelected,
            )
        }
    }
}

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
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            // no-op; spaced by 16.dp in the LazyColumn
        }

        items(agents) { (header, row) ->
            AgentRow(header, row, onAgentSelected)
        }

        item {
            // no-op; spaced by 16.dp in the LazyColumn
        }
    }
}

@Composable
fun AgentRow(
    category: String,
    agents: List<AgentDefinition>,
    onAgentSelected: (AgentDefinition) -> Unit,
) {
    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, bottom = 8.dp),
            text = category,
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                // no-op; spaced by 16.dp in the LazyRow
            }

            items(agents) { agent ->
                AgentCard(agent, onAgentSelected)
            }

            item {
                // no-op; spaced by 16.dp in the LazyRow
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
            .requiredSize(128.dp),
        elevation = 4.dp,
        onClick = {
            onAgentSelected(agent)
        },
    ) {
        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                text = agent.name,
                textAlign = TextAlign.Center,
            )
        }
    }
}
