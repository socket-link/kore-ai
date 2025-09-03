package link.socket.kore.ui.model

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.max
import link.socket.kore.domain.model.llm.ModelFeatures
import link.socket.kore.domain.model.llm.TokenCount
import link.socket.kore.domain.model.llm.TokenRate
import link.socket.kore.domain.model.tool.ProvidedTool

enum class UserTier(val displayName: String) {
    FREE("Free"),
    TIER_1("Tier 1"),
    TIER_2("Tier 2"),
    TIER_3("Tier 3"),
    TIER_4("Tier 4"),
    TIER_5("Tier 5")
}

@Composable
fun ModelFeaturesDisplay(
    features: ModelFeatures,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                style = MaterialTheme.typography.h6,
                text = "Use Cases",
            )

            // Supported Inputs
            SupportedInputsSection(features.supportedInputs)
            
            // Available Tools
            if (features.availableTools.isNotEmpty()) {
                ToolsSection(features.availableTools)
            }
            
            // Limits Overview
            features.limits?.let { limits ->
                LimitsSection(limits)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Rate Limits per Tier
                RateLimitsSection(limits.rate)
            }
            
            // Training Cutoff
            TrainingCutoffSection(features.trainingCutoffDate)
        }
    }
}

@Composable
fun PerformanceChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .background(
                    color = color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    BorderStroke(1.dp, color.copy(alpha = 0.3f)),
                    RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.body2,
                color = color,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SupportedInputsSection(supportedInputs: ModelFeatures.SupportedInputs) {
    Column {
        Text(
            text = "Supported Inputs",
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InputTypeChip("Text", supportedInputs.text)
            InputTypeChip("Image", supportedInputs.image)
            InputTypeChip("PDF", supportedInputs.pdf)
            InputTypeChip("Audio", supportedInputs.audio)
            InputTypeChip("Video", supportedInputs.video)
        }
    }
}

@Composable
private fun InputTypeChip(type: String, isSupported: Boolean) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSupported) Color(0xFF4CAF50).copy(alpha = 0.1f) 
                       else MaterialTheme.colors.onSurface.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                BorderStroke(
                    1.dp, 
                    if (isSupported) Color(0xFF4CAF50).copy(alpha = 0.3f)
                    else MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                ),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = type,
            style = MaterialTheme.typography.caption,
            color = if (isSupported) Color(0xFF4CAF50)
                   else MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
            fontWeight = if (isSupported) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun ToolsSection(tools: List<ProvidedTool<*>>) {
    Column {
        Text(
            text = "Available Tools",
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(tools) { tool ->
                ToolChip(tool::class.simpleName ?: "Tool")
            }
        }
    }
}

@Composable
private fun ToolChip(toolName: String) {
    Box(
        modifier = Modifier
            .background(
                color = Color(0xFF2196F3).copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                BorderStroke(1.dp, Color(0xFF2196F3).copy(alpha = 0.3f)),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = toolName,
            style = MaterialTheme.typography.caption,
            color = Color(0xFF2196F3),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun LimitsSection(limits: ModelFeatures.Limits) {
    Column {
        Text(
            text = "Token Limits",
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Token Scale Visualization
        TokenScaleVisualization(
            contextWindow = limits.token.contextWindow,
            maxOutput = limits.token.maxOutput,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LimitItem(
                label = "Context Window",
                value = "${limits.token.contextWindow.label} tokens"
            )
            
            LimitItem(
                label = "Max Output",
                value = "${limits.token.maxOutput.label} tokens"
            )
        }
    }
}

@Composable
private fun LimitItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TokenScaleVisualization(
    contextWindow: TokenCount,
    maxOutput: TokenCount,
    modifier: Modifier = Modifier
) {
    val contextValue = getTokenNumericValue(contextWindow)
    val outputValue = getTokenNumericValue(maxOutput)
    val maxValue = max(contextValue, outputValue)
    
    // Calculate relative progress values (0.0 to 1.0)
    val contextProgress = if (maxValue > 0) (contextValue.toFloat() / maxValue.toFloat()) else 0f
    val outputProgress = if (maxValue > 0) (outputValue.toFloat() / maxValue.toFloat()) else 0f
    
    Column(modifier = modifier) {
        // Context Window Visualization
        TokenBar(
            label = "Context Window",
            value = contextWindow.label,
            progress = contextProgress,
            color = Color(0xFF4CAF50),
            isLarger = contextValue >= outputValue
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Max Output Visualization
        TokenBar(
            label = "Max Output",
            value = maxOutput.label,
            progress = outputProgress,
            color = Color(0xFF2196F3),
            isLarger = outputValue > contextValue
        )
    }
}

@Composable
private fun TokenBar(
    label: String,
    value: String,
    progress: Float,
    color: Color,
    isLarger: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.width(80.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .background(
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.caption,
            fontWeight = if (isLarger) FontWeight.Bold else FontWeight.Normal,
            color = if (isLarger) color else MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.End,
            modifier = Modifier.width(60.dp)
        )
    }
}

private fun getTokenNumericValue(tokenCount: TokenCount): Long {
    return when (tokenCount) {
        TokenCount._4k -> 4_000L
        TokenCount._4096 -> 4_096L
        TokenCount._8k -> 8_000L
        TokenCount._8192 -> 8_192L
        TokenCount._10k -> 10_000L
        TokenCount._15k -> 15_000L
        TokenCount._16k -> 16_000L
        TokenCount._20k -> 20_000L
        TokenCount._25k -> 25_000L
        TokenCount._30k -> 30_000L
        TokenCount._32k -> 32_000L
        TokenCount._35k -> 35_000L
        TokenCount._40k -> 40_000L
        TokenCount._50k -> 50_000L
        TokenCount._64k -> 64_000L
        TokenCount._80k -> 80_000L
        TokenCount._90k -> 90_000L
        TokenCount._100k -> 100_000L
        TokenCount._128k -> 128_000L
        TokenCount._160k -> 160_000L
        TokenCount._200k -> 200_000L
        TokenCount._250k -> 250_000L
        TokenCount._400k -> 400_000L
        TokenCount._450k -> 450_000L
        TokenCount._800k -> 800_000L
        TokenCount._1m -> 1_000_000L
        TokenCount._2m -> 2_000_000L
        TokenCount._3m -> 3_000_000L
        TokenCount._4m -> 4_000_000L
        TokenCount._5m -> 5_000_000L
        TokenCount._8m -> 8_000_000L
        TokenCount._10m -> 10_000_000L
        TokenCount._30m -> 30_000_000L
        TokenCount._40m -> 40_000_000L
        TokenCount._150m -> 150_000_000L
        TokenCount._180m -> 180_000_000L
        TokenCount._400m -> 400_000_000L
        TokenCount._500m -> 500_000_000L
        TokenCount._1b -> 1_000_000_000L
        TokenCount._5b -> 5_000_000_000L
    }
}

@Composable
private fun RateLimitsSection(rateLimits: ModelFeatures.Limits.RateLimits) {
    var selectedTier by remember { mutableStateOf(UserTier.TIER_1) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    
    // Get available tiers from rate limits
    val availableTiers = buildList {
        if (rateLimits.tierFree != null) add(UserTier.FREE)
        if (rateLimits.tier1 != null) add(UserTier.TIER_1)
        if (rateLimits.tier2 != null) add(UserTier.TIER_2)
        if (rateLimits.tier3 != null) add(UserTier.TIER_3)
        if (rateLimits.tier4 != null) add(UserTier.TIER_4)
        if (rateLimits.tier5 != null) add(UserTier.TIER_5)
    }
    
    // Get selected tier data
    val selectedTierData = when (selectedTier) {
        UserTier.FREE -> rateLimits.tierFree
        UserTier.TIER_1 -> rateLimits.tier1
        UserTier.TIER_2 -> rateLimits.tier2
        UserTier.TIER_3 -> rateLimits.tier3
        UserTier.TIER_4 -> rateLimits.tier4
        UserTier.TIER_5 -> rateLimits.tier5
    }
    
    Column {
        Text(
            text = "Rate Limits",
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Tier Selection Dropdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Select Tier:",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
            )
            
            Box {
                OutlinedButton(
                    onClick = { dropdownExpanded = true },
                    modifier = Modifier.width(120.dp)
                ) {
                    Text(
                        text = selectedTier.displayName,
                        style = MaterialTheme.typography.body2
                    )
                }
                
                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    availableTiers.forEach { tier ->
                        DropdownMenuItem(
                            onClick = {
                                selectedTier = tier
                                dropdownExpanded = false
                            }
                        ) {
                            Text(text = tier.displayName)
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Display rate limit information for selected tier
        selectedTierData?.let { tierData ->
            RateLimitDetails(tierData)
        } ?: run {
            Text(
                text = "No rate limit data available for this tier",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun RateLimitDetails(tierData: ModelFeatures.Limits.RateLimits.Tier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Request Limits
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RateLimitItem(
                label = "Requests/Min",
                value = tierData.requestsPerMinute.toString(),
                color = Color(0xFF9C27B0)
            )
            
            tierData.requestsPerDay?.let { rpd ->
                RateLimitItem(
                    label = "Requests/Day",
                    value = rpd.toString(),
                    color = Color(0xFF9C27B0)
                )
            }
        }
        
        // Token Rate Limits
        when (val tokenRate = tierData.tokenRate) {
            is TokenRate.Combined -> {
                RateLimitItem(
                    label = "Tokens/Min",
                    value = tokenRate.tokensPerMinute.label,
                    color = Color(0xFF4CAF50)
                )
            }
            is TokenRate.Separated -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Token Limits (per minute)",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RateLimitItem(
                            label = "Input Tokens",
                            value = tokenRate.inputTokensPerMinute.label,
                            color = Color(0xFF4CAF50)
                        )
                        
                        RateLimitItem(
                            label = "Output Tokens",
                            value = tokenRate.outputTokensPerMinute.label,
                            color = Color(0xFF2196F3)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RateLimitItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .background(
                    color = color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    BorderStroke(1.dp, color.copy(alpha = 0.3f)),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.body2,
                color = color,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TrainingCutoffSection(cutoffDate: io.ktor.util.date.GMTDate) {
    Column {
        Text(
            text = "Training Data",
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        Text(
            text = "Updated through ${cutoffDate.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${cutoffDate.year}",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )
    }
}
