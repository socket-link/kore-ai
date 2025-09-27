package link.socket.kore.ui.agent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import link.socket.kore.domain.agent.AgentInput
import link.socket.kore.domain.agent.bundled.AgentDefinition
import link.socket.kore.domain.agent.bundled.codeAgents
import link.socket.kore.domain.agent.bundled.generalAgents
import link.socket.kore.domain.agent.bundled.promptAgents
import link.socket.kore.domain.agent.bundled.reasoningAgents

@Composable
fun AgentSelectionSection(
    partiallySelectedAgent: AgentDefinition?,
    setAgentPartiallySelected: (AgentDefinition) -> Unit,
    onCreateAgent: (AgentDefinition) -> Unit,
    modifier: Modifier = Modifier,
) {
   val requiredInputs: MutableState<List<AgentInput>> = remember(partiallySelectedAgent) {
       mutableStateOf(partiallySelectedAgent?.requiredInputs ?: emptyList())
   }

    val optionalInputs: MutableState<List<AgentInput>> = remember(partiallySelectedAgent) {
        mutableStateOf(partiallySelectedAgent?.optionalInputs ?: emptyList())
    }

    val onAgentSelected: (AgentDefinition) -> Unit = { agentDefinition ->
        if (agentDefinition.requiredInputs.isNotEmpty()) {
            setAgentPartiallySelected(agentDefinition)
        } else {
            onCreateAgent(agentDefinition)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        AgentColumn(
            onAgentSelected = onAgentSelected,
        )

        AgentInputsSection(
            requiredInputs = requiredInputs.value,
            optionalInputs = optionalInputs.value,
            onAgentSubmission = {
                // TODO: Modify AgentDefinition to accept inputs
                onCreateAgent(partiallySelectedAgent!!)
            },
        )
    }
}

@Composable
fun AgentColumn(
    onAgentSelected: (AgentDefinition) -> Unit,
    modifier: Modifier = Modifier,
) {
    val agents = listOf(
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
private fun AgentRow(
    category: String,
    agents: List<AgentDefinition>,
    onAgentSelected: (AgentDefinition) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isExpanded: MutableState<Boolean> = remember { mutableStateOf(true) }
    
    Column {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            onClick = { isExpanded.value = !isExpanded.value }
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "$category ${if (isExpanded.value) "▼" else "▶"}",
            )
        }

        if (isExpanded.value) {
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
                            isExpanded.value = false
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
