package link.socket.kore.ui.model.selection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.Checkbox
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.round
import link.socket.kore.ui.theme.themeColors
import link.socket.kore.ui.theme.themeShapes
import link.socket.kore.ui.theme.themeTypography

@Composable
fun ModelFiltersSection(
    state: FilterState,
    onStateChange: (FilterState) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = themeColors()
    val typography = themeTypography()

    Column(
        modifier = modifier
            .background(colors.background)
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                style = typography.h5,
                text = "Advanced Filters",
            )

            OutlinedButton(
                onClick = onReset,
            ) {
                Text(
                    text = "Reset",
                )
            }
        }

        Spacer(Modifier.requiredHeight(4.dp))

        Text(
            style = typography.overline,
            color = Color(0xFF9CA3AF),
            text = "SET CUSTOM CONSTRAINTS",
        )

        Spacer(Modifier.requiredHeight(12.dp))

        FilterSlider(
            title = "Reasoning",
            emoji = "🧠",
            value = state.minReasoning,
            valueRange = 0f..100f,
            steps = 9, // 10 discrete values: 0, 10, 20, ..., 100
            onChange = {
                onStateChange(state.copy(minReasoning = it))
            },
        )

        Spacer(Modifier.requiredHeight(4.dp))

        FilterSlider(
            title = "Speed",
            emoji = "⏳",
            subtitle = "ms / run",
            value = state.maxLatencyMs,
            valueRange = 50f..3000f,
            steps = 58, // Steps of ~50ms intervals
            onChange = {
                onStateChange(state.copy(maxLatencyMs = it))
            },
        )

        Spacer(Modifier.requiredHeight(4.dp))

        FilterSlider(
            title = "Cost",
            emoji = "💵",
            subtitle = "$ / 100 runs",
            prefix = "$",
            value = state.maxCost,
            valueRange = 0.01f..10f,
            steps = 19, // 20 discrete values from $0.01 to $10
            onChange = {
                onStateChange(state.copy(maxCost = it))
            },
        )

        Spacer(Modifier.requiredHeight(4.dp))

        ProviderChips(
            title = "Model Providers",
            providers = state.providers,
            onToggle = { key ->
                val updatedProviders = state.providers.map { provider ->
                    if (provider.key == key) {
                        provider.copy(isSelected = !provider.isSelected)
                    } else {
                        provider
                    }
                }

                onStateChange(
                    state.copy(providers = updatedProviders)
                )
            }
        )

        Spacer(Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = state.showUnsuitable,
                onCheckedChange = { isChecked ->
                    onStateChange(
                        state.copy(showUnsuitable = isChecked)
                    )
                },
            )

            Text(
                style = typography.body1,
                text = "Show unsuitable models",
            )
        }
    }
}

@Composable
private fun FilterSlider(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit,
    steps: Int = 0,
    prefix: String? = null,
    emoji: String? = null,
    subtitle: String? = null,
    enabled: Boolean = true,
    onEnabledChange: ((Boolean) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val colors = themeColors()
    val typography = themeTypography()
    val shapes = themeShapes()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shapes.medium)
            .background(colors.surface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (emoji != null) {
                    Text(
                        modifier = Modifier
                            .padding(end = 8.dp),
                        text = emoji,
                    )
                }

                Column {
                    Text(
                        style = typography.subtitle1,
                        fontWeight = FontWeight.Medium,
                        text = title,
                    )
                    if (subtitle != null) {
                        Text(
                            style = typography.caption,
                            color = Color(0xFF9CA3AF),
                            text = subtitle,
                        )
                    }
                }
            }

            if (onEnabledChange != null) {
                Checkbox(
                    checked = enabled,
                    onCheckedChange = onEnabledChange,
                )
            }
        }

        if (enabled) {
            Slider(
                value = value,
                valueRange = valueRange,
                steps = steps,
                onValueChange = onChange,
                colors = SliderDefaults.colors(
                    thumbColor = colors.primary,
                ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    style = typography.caption,
                    text = formatValue(valueRange.start, prefix),
                )

                Text(
                    style = typography.caption,
                    text = formatValue(valueRange.endInclusive, prefix),
                )
            }
        }
    }
}

@Composable
private fun ProviderChips(
    title: String,
    providers: List<ProviderFilter>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = themeColors()
    val typography = themeTypography()
    val shapes = themeShapes()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shapes.medium)
            .background(colors.surface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                style = typography.subtitle1,
                fontWeight = FontWeight.Medium,
                text = title,
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = spacedBy(8.dp),
        ) {
            providers.forEach { item ->
                SelectableChip(
                    isSelected = item.isSelected,
                    text = item.label,
                    onClick = {
                        onToggle(item.key)
                    },
                )
            }
        }
    }
}

@Composable
private fun SelectableChip(
    isSelected: Boolean,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = themeColors()
    val typography = themeTypography()
    val shapes = themeShapes()

    val bgColor = remember(colors, isSelected) {
        if (isSelected) {
            colors.onBackground
        } else {
            colors.onSurface
        }
    }

    Text(
        modifier = modifier
            .clip(shapes.large)
            .background(bgColor)
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp,
            )
            .clickable {
                onClick()
            },
        style = typography.caption,
        color = colors.onPrimary,
        text = text,
    )
}

private fun formatValue(
    value: Float,
    prefix: String?,
): String {
    val base: String = when {
        value >= 1000f -> "${(value / 1000f).toInt()}k"
        value == value.toLong().toFloat() -> value.toInt().toString()
        else -> {
            // Round to 2 decimal places and format
            val scaled = round(value * 100f) / 100f
            val intPart = scaled.toInt()
            val decimalPart = ((scaled - intPart) * 100).toInt()
            when {
                decimalPart == 0 -> intPart.toString()
                decimalPart % 10 == 0 -> "$intPart.${decimalPart / 10}"
                else -> "$intPart.${decimalPart.toString().padStart(2, '0')}"
            }
        }
    }

    return (prefix ?: "") + base
}
