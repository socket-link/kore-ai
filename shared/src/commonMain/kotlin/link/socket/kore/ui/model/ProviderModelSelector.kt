package link.socket.kore.ui.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import link.socket.kore.domain.model.llm.AI_Provider
import link.socket.kore.domain.model.llm.LLM
import link.socket.kore.domain.model.llm.ModelFeatures.RelativeReasoning
import link.socket.kore.domain.model.llm.ModelFeatures.RelativeSpeed

@Composable
fun ProviderModelSelector(
    modifier: Modifier = Modifier,
    selectedProvider: AI_Provider<*, *>?,
    selectedModel: LLM<*>?,
    selectableProviders: List<AI_Provider<*, *>>,
    selectableModels: List<LLM<*>>?,
    onProviderSelected: (AI_Provider<*, *>) -> Unit,
    onModelSelected: (LLM<*>) -> Unit,
) {
    var providerMenuExpanded by remember { mutableStateOf(false) }
    var modelMenuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.padding(16.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                style = MaterialTheme.typography.h6,
                text = "Model Selection",
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
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

                Spacer(modifier = Modifier.padding(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        modifier = Modifier.padding(bottom = 4.dp),
                        style = MaterialTheme.typography.subtitle1,
                        text = "AI Model",
                    )

                    val modelLabel = try {
                        selectedModel?.name ?: "Select model"
                    } catch (e: Throwable) {
                        // Fallback if reading name triggers init-time exception
                        "Select model"
                    }

                    OutlinedButton(
                        modifier = Modifier
                            .requiredHeight(48.dp)
                            .fillMaxWidth(),
                        onClick = {
                            val notModelsToSelect = (selectableModels == null) || selectableModels.isEmpty()
                            if (!notModelsToSelect) {
                                modelMenuExpanded = true
                            }
                        },
                    ) {
                        Text(text = modelLabel)
                    }

                    Spacer(modifier = Modifier.padding(8.dp))

                    DropdownMenu(
                        expanded = modelMenuExpanded,
                        onDismissRequest = {
                            modelMenuExpanded = false
                        },
                    ) {
                        selectableModels?.forEach { model ->
                            DropdownMenuItem(
                                onClick = {
                                    modelMenuExpanded = false
                                    onModelSelected(model)
                                },
                            ) {
                                val safeName = try {
                                    model.displayName
                                } catch (e: Throwable) {
                                    "Unnamed model"
                                }
                                Text(
                                    style = MaterialTheme.typography.subtitle2,
                                    text = safeName,
                                )
                            }
                        }
                    }
                }
            }

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "Model Overview",
                style = MaterialTheme.typography.subtitle1,
            )

            // Performance Metrics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PerformanceChip(
                    label = "Reasoning",
                    value = selectedModel?.features?.reasoningLevel?.name ?: "N/A",
                    color = when (selectedModel?.features?.reasoningLevel) {
                        RelativeReasoning.HIGH -> Color(0xFF4CAF50)
                        RelativeReasoning.NORMAL -> Color(0xFFFF9800)
                        RelativeReasoning.LOW -> Color(0xFFF44336)
                        null -> Color.Gray
                    }
                )

                PerformanceChip(
                    label = "Speed",
                    value = selectedModel?.features?.speed?.name ?: "N/A",
                    color = when (selectedModel?.features?.speed) {
                        RelativeSpeed.FAST -> Color(0xFF4CAF50)
                        RelativeSpeed.NORMAL -> Color(0xFFFF9800)
                        RelativeSpeed.SLOW -> Color(0xFFF44336)
                        null -> Color.Gray
                    }
                )
            }
        }
    }
}
