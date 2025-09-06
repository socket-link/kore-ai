package link.socket.kore.ui.agent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import link.socket.kore.domain.agent.definition.AgentDefinition
import link.socket.kore.ui.model.ModelTools
import link.socket.kore.ui.theme.themeTypography

@Composable
fun AgentDetails(
    agentDefinition: AgentDefinition,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(
                    horizontal = 64.dp,
                    vertical = 24.dp,
                ),
        ) {
            // Display the agent name
            Text(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth(),
                style = themeTypography().h6,
                text = "${agentDefinition.name} Agent",
                textAlign = TextAlign.Start,
            )

            Text(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.caption,
                text = agentDefinition.description,
                textAlign = TextAlign.Start,
            )

            Text(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.subtitle2,
                text = "Required Capabilities",
                textAlign = TextAlign.Start,
                fontWeight = MaterialTheme.typography.subtitle2.fontWeight,
            )

            val features = remember(agentDefinition) {
                agentDefinition.aiConfiguration.selectedLLM?.features
            }

            // Available Tools
            if (features?.availableTools?.isNotEmpty() == true) {
                ModelTools(features.availableTools)
            }
        }
    }
}
