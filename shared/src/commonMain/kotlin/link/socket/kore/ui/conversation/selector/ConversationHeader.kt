package link.socket.kore.ui.conversation.selector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.model.agent.KoreAgent
import link.socket.kore.ui.widget.header.Header
import link.socket.kore.ui.widget.header.SelectionConfig

sealed class AgentSelectionState {

    data class Unselected(
        val agentList: List<KoreAgent>,
    ) : AgentSelectionState() {
        override val agentName: String? = null
    }

    data class PartiallySelected(
        val agent: KoreAgent,
        val neededInputs: List<AgentInput>,
    ) : AgentSelectionState() {
        override val agentName: String = agent.name
    }

    data class Selected(
        val agent: KoreAgent,
    ) : AgentSelectionState() {
        override val agentName: String = agent.name
    }

    abstract val agentName: String?
}

sealed class AgentInput(
    open val key: String,
    open val value: String,
) {
    data class StringArg(
        override val key: String,
        override val value: String,
    ) : AgentInput(key, value)

    data class ListArg(
        override val key: String,
        val textFieldLabel: String,
        val listValue: List<String>,
    ) : AgentInput(key, listValue.joinToString(", "))
}

@Composable
fun ConversationHeader(
    modifier: Modifier = Modifier,
    selectionState: AgentSelectionState,
    drawerExpanded: Boolean,
    onExpandDrawer: () -> Unit,
    onAgentSelected: (KoreAgent) -> Unit,
    onHeaderAgentSubmission: (AgentSelectionState.PartiallySelected) -> Unit,
    onBackClicked: () -> Unit,
) {
    val selectionEnabled = remember(selectionState) {
        derivedStateOf { selectionState !is AgentSelectionState.Unselected }
    }

    Surface(
        modifier = modifier,
        elevation = 16.dp,
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
        ) {
            Header(
                selectionConfig = SelectionConfig(
                    selectionEnabled = selectionEnabled.value,
                    selectedTitle = "${selectionState.agentName} Agent",
                    firstOption = "Select an Agent",
                    secondOption = "Create Your Own",
                    onSecondOptionSelected = {
                        // TODO: Navigation to Agent creation
                    },
                ),
                displayBackIcon = true,
                displayMenuIcon = selectionState is AgentSelectionState.Unselected,
                drawerExpanded = drawerExpanded,
                onExpandDrawer = onExpandDrawer,
                onBackClicked = onBackClicked,
            )

            when (selectionState) {
                is AgentSelectionState.Unselected -> {
                    ConversationAgentSelector(
                        modifier = Modifier
                            .fillMaxWidth(),
                        drawerExpanded = drawerExpanded,
                        agentList = selectionState.agentList,
                        onAgentSelected = onAgentSelected,
                    )
                }
                is AgentSelectionState.PartiallySelected -> {
                    ConversationAgentSetup(
                        modifier = Modifier
                            .fillMaxWidth(),
                        selectionState = selectionState,
                        onHeaderAgentSubmission = onHeaderAgentSubmission,
                    )
                }
                is AgentSelectionState.Selected -> {
                    // no-op
                }
            }
        }
    }
}
