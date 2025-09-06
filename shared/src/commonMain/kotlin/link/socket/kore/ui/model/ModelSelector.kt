package link.socket.kore.ui.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.domain.model.llm.AI_Provider
import link.socket.kore.domain.model.llm.LLM

@Composable
fun ModelSelector(
    selectedProvider: AI_Provider<*, *>?,
    selectedModel: LLM<*>?,
    selectableProviders: List<AI_Provider<*, *>>,
    selectableModels: List<LLM<*>>?,
    suggestedModels: List<Pair<AI_Provider<*, *>, LLM<*>>>?,
    onProviderSelected: (AI_Provider<*, *>) -> Unit,
    onModelSelected: (LLM<*>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SuggestedModelList(
            selectedModelId = selectedModel?.name.orEmpty(),
            suggestedModels = suggestedModels,
            onModelSelected = onModelSelected,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            ProviderDropdownSelector(
                selectedProvider = selectedProvider,
                selectableProviders = selectableProviders,
                onProviderSelected = onProviderSelected,
            )

            Spacer(modifier = Modifier.padding(8.dp))

            ModelDropdownSelector(
                selectedModel = selectedModel,
                selectableModels = selectableModels ?: emptyList(),
                onModelSelected = onModelSelected,
            )
        }
    }
}

@Composable
private fun ProviderDropdownSelector(
    selectedProvider: AI_Provider<*, *>?,
    selectableProviders: List<AI_Provider<*, *>>,
    onProviderSelected: (AI_Provider<*, *>) -> Unit,
) {
    var providerMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth(0.5f),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            style = MaterialTheme.typography.subtitle1,
            text = "AI Provider",
        )

        OutlinedButton(
            modifier = Modifier
                .requiredHeight(48.dp)
                .fillMaxWidth(),
            onClick = {
                providerMenuExpanded = true
            },
        ) {
            Text(text = selectedProvider?.name.orEmpty())
        }

        Spacer(modifier = Modifier.padding(8.dp))

        DropdownMenu(
            expanded = providerMenuExpanded,
            onDismissRequest = {
                providerMenuExpanded = false
            },
        ) {
            selectableProviders.forEach { provider ->
                DropdownMenuItem(
                    onClick = {
                        providerMenuExpanded = false
                        onProviderSelected(provider)
                    },
                ) {
                    Text(
                        style = MaterialTheme.typography.subtitle2,
                        text = provider.name,
                    )
                }
            }
        }
    }
}

@Composable
private fun ModelDropdownSelector(
    selectedModel: LLM<*>?,
    selectableModels: List<LLM<*>>,
    onModelSelected: (LLM<*>) -> Unit,
) {
    var modelMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            style = MaterialTheme.typography.subtitle1,
            text = "AI Model",
        )

        OutlinedButton(
            modifier = Modifier
                .requiredHeight(48.dp)
                .fillMaxWidth(),
            onClick = {
                val notModelsToSelect = selectableModels.isEmpty()
                if (!notModelsToSelect) {
                    modelMenuExpanded = true
                }
            },
        ) {
            Text(text = selectedModel?.name ?: "Select model")
        }

        Spacer(modifier = Modifier.padding(8.dp))

        DropdownMenu(
            expanded = modelMenuExpanded,
            onDismissRequest = {
                modelMenuExpanded = false
            },
        ) {
            selectableModels.forEach { model ->
                DropdownMenuItem(
                    onClick = {
                        modelMenuExpanded = false
                        onModelSelected(model)
                    },
                ) {
                    Text(
                        style = MaterialTheme.typography.subtitle2,
                        text = model.displayName,
                    )
                }
            }
        }
    }
}
