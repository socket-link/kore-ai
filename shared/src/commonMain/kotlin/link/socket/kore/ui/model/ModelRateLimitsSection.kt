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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import link.socket.kore.domain.ai.UsageTier
import link.socket.kore.domain.limits.RateLimits
import link.socket.kore.domain.limits.Tier
import link.socket.kore.domain.limits.TokenRate

@Composable
fun ModelRateLimitsSection(
    rateLimits: RateLimits,
    modifier: Modifier = Modifier,
) {
    val selectedTier: MutableState<UsageTier> = remember {
        mutableStateOf(UsageTier.FREE)
    }
    val dropdownExpanded: MutableState<Boolean> = remember {
        mutableStateOf(false)
    }

    val availableTiers: List<UsageTier> = buildList {
        if (rateLimits.tierFree != null) add(UsageTier.FREE)
        if (rateLimits.tier1 != null) add(UsageTier.TIER_1)
        if (rateLimits.tier2 != null) add(UsageTier.TIER_2)
        if (rateLimits.tier3 != null) add(UsageTier.TIER_3)
        if (rateLimits.tier4 != null) add(UsageTier.TIER_4)
        if (rateLimits.tier5 != null) add(UsageTier.TIER_5)
    }

    val selectedTierData: Tier? = when (selectedTier.value) {
        UsageTier.FREE -> rateLimits.tierFree
        UsageTier.TIER_1 -> rateLimits.tier1
        UsageTier.TIER_2 -> rateLimits.tier2
        UsageTier.TIER_3 -> rateLimits.tier3
        UsageTier.TIER_4 -> rateLimits.tier4
        UsageTier.TIER_5 -> rateLimits.tier5
    }

    Column(modifier) {
        Text(
            modifier = Modifier.padding(bottom = 12.dp),
            text = "Rate Limits",
            style = MaterialTheme.typography.subtitle2,
            fontWeight = FontWeight.Bold,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Select Tier:",
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
            )

            Box {
                OutlinedButton(
                    modifier = Modifier.width(120.dp),
                    onClick = {
                        dropdownExpanded.value = true
                    },
                ) {
                    Text(
                        text = selectedTier.value.displayName,
                        style = MaterialTheme.typography.body2,
                    )
                }

                DropdownMenu(
                    expanded = dropdownExpanded.value,
                    onDismissRequest = {
                        dropdownExpanded.value = false
                    },
                ) {
                    availableTiers.forEach { tier ->
                        DropdownMenuItem(
                            onClick = {
                                selectedTier.value = tier
                                dropdownExpanded.value = false
                            }
                        ) {
                            Text(text = tier.displayName)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        selectedTierData?.let { tierData ->
            RateLimitDetails(tierData)
        } ?: run {
            Text(
                text = "No rate limit data available for this tier",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun RateLimitDetails(
    tierData: Tier,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            RateLimitItem(
                label = "Requests/Min",
                value = tierData.requestsPerMinute.toString(),
                color = Color(0xFF9C27B0),
            )

            tierData.requestsPerDay?.let { rpd ->
                RateLimitItem(
                    label = "Requests/Day",
                    value = rpd.toString(),
                    color = Color(0xFF9C27B0),
                )
            }
        }

        when (val tokenRate = tierData.tokenRate) {
            is TokenRate.Combined -> {
                RateLimitItem(
                    label = "Tokens/Min",
                    value = tokenRate.tokensPerMinute.label,
                    color = Color(0xFF4CAF50),
                )
            }
            is TokenRate.Separated -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Token Limits (per minute)",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium,
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        RateLimitItem(
                            label = "Input Tokens",
                            value = tokenRate.inputTokensPerMinute.label,
                            color = Color(0xFF4CAF50),
                        )

                        RateLimitItem(
                            label = "Output Tokens",
                            value = tokenRate.outputTokensPerMinute.label,
                            color = Color(0xFF2196F3),
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
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .background(
                    color = color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = color.copy(alpha = 0.3f),
                    ),
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.body2,
                color = color,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
