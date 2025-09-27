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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.domain.ai.model.AIModel
import link.socket.kore.domain.ai.provider.AIProvider

@Composable
fun ModelSelectionSection(
    selectedProvider: AIProvider<*, *>?,
    selectedModel: AIModel?,
    selectableProviders: List<AIProvider<*, *>>,
    selectableModels: List<AIModel>?,
    suggestedModels: List<Pair<AIProvider<*, *>, AIModel>>?,
    onProviderSelected: (AIProvider<*, *>) -> Unit,
    onModelSelected: (AIModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SuggestedModelsSection(
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
    selectedProvider: AIProvider<*, *>?,
    selectableProviders: List<AIProvider<*, *>>,
    onProviderSelected: (AIProvider<*, *>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val providerMenuExpanded: MutableState<Boolean> = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
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
                providerMenuExpanded.value = true
            },
        ) {
            Text(text = selectedProvider?.name.orEmpty())
        }

        Spacer(modifier = Modifier.padding(8.dp))

        DropdownMenu(
            expanded = providerMenuExpanded.value,
            onDismissRequest = {
                providerMenuExpanded.value = false
            },
        ) {
            selectableProviders.forEach { provider ->
                DropdownMenuItem(
                    onClick = {
                        providerMenuExpanded.value = false
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
    selectedModel: AIModel?,
    selectableModels: List<AIModel>,
    onModelSelected: (AIModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val modelMenuExpanded: MutableState<Boolean> = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
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
                val noModelsToSelect = selectableModels.isEmpty()
                if (!noModelsToSelect) {
                    modelMenuExpanded.value = true
                }
            },
        ) {
            Text(text = selectedModel?.name ?: "Select model")
        }

        Spacer(modifier = Modifier.padding(8.dp))

        DropdownMenu(
            expanded = modelMenuExpanded.value,
            onDismissRequest = {
                modelMenuExpanded.value = false
            },
        ) {
            selectableModels.forEach { model ->
                DropdownMenuItem(
                    onClick = {
                        modelMenuExpanded.value = false
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
