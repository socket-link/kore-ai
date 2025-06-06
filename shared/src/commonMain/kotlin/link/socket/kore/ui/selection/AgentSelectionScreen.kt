package link.socket.kore.ui.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import link.socket.kore.model.agent.AgentDefinition
import link.socket.kore.model.agent.AgentInput
import link.socket.kore.model.agent.bundled.codeAgents
import link.socket.kore.model.agent.bundled.generalAgents
import link.socket.kore.model.agent.bundled.promptAgents
import link.socket.kore.model.agent.bundled.reasoningAgents
import link.socket.kore.ui.widget.header.Header

/**
 * Composable function to display the agent selection screen.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param onSubmit Callback function to handle agent submission.
 * @param onBackClicked Callback function to handle back button click.
 */
@Composable
fun AgentSelectionScreen(
    modifier: Modifier = Modifier,
    onSubmit: (AgentDefinition) -> Unit,
    onBackClicked: () -> Unit,
) {
    var partiallySelectedAgent by remember { mutableStateOf<AgentDefinition?>(null) }

    val neededInputs: List<AgentInput> by remember(partiallySelectedAgent) {
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
        modifier = modifier
            .fillMaxSize(),
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
                        title = "Agent Selection",
                        displayBackIcon = true,
                        onBackClicked = onBackClicked,
                    )
                }
            }
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .padding(
                        end = 8.dp,
                        bottom = 16.dp,
                    ),
            ) {
                FloatingActionButton(
                    onClick = {
                        // Create new Agent screen
                    },
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        if (neededInputs.isEmpty()) {
            AgentColumn(
                modifier = Modifier.padding(paddingValues),
                onAgentSelected = onAgentSelected,
            )
        } else {
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
    }
}

/**
 * Composable function to display a column of agents.
 *
 * @param modifier Modifier to be applied to the layout.
 * @param onAgentSelected Callback function to handle agent selection.
 */
@Composable
fun AgentColumn(
    modifier: Modifier = Modifier,
    onAgentSelected: (AgentDefinition) -> Unit,
) {
    val data =
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

        items(data) { (header, row) ->
            AgentRow(header, row, onAgentSelected)
        }

        item {
            // no-op; spaced by 16.dp in the LazyColumn
        }
    }
}

/**
 * Composable function to display a row of agents.
 *
 * @param category The category of agents.
 * @param agents List of agent definitions.
 * @param onAgentSelected Callback function to handle agent selection.
 */
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

/**
 * Composable function to display an agent card.
 *
 * @param agent The agent definition.
 * @param onAgentSelected Callback function to handle agent selection.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AgentCard(
    agent: AgentDefinition,
    onAgentSelected: (AgentDefinition) -> Unit,
) {
    Card(
        modifier = Modifier
            .requiredSize(200.dp),
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
