package link.socket.kore.ui.agent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import link.socket.kore.domain.agent.AgentInput
import link.socket.kore.domain.agent.bundled.AgentDefinition

@Composable
fun AgentCreationSection(
    partiallySelectedAgent: AgentDefinition?,
    onAgentPartiallySelected: (AgentDefinition) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        partiallySelectedAgent?.let { definition ->
            // Agent is partially selected, so display details and inputs
            AgentDetailsSection(
                modifier = Modifier
                    .fillMaxWidth(),
                agentDefinition = definition,
            )

            val requiredInputs: MutableState<List<AgentInput>> = remember(definition) {
                mutableStateOf(definition.requiredInputs)
            }

            val optionalInputs: MutableState<List<AgentInput>> = remember(definition) {
                mutableStateOf(definition.optionalInputs)
            }

            AgentInputsSection(
                requiredInputs = requiredInputs.value,
                optionalInputs = optionalInputs.value,
                onAgentInputsUpdated = { setInputs: Map<String, AgentInput> ->
                    // TODO: call onCreateAgent(partiallySelectedAgent!!)
                },
            )
        } ?: run {
            // No agent is selected, so display the available agent list
            AgentSelectionSection(
                modifier = Modifier
                    .fillMaxWidth(),
                onAgentPartiallySelected = onAgentPartiallySelected,
            )
        }
    }
}
