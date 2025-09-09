package link.socket.kore.ui.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import link.socket.kore.domain.model.ModelFeatures

enum class UserTier(val displayName: String) {
    FREE("Free"),
    TIER_1("Tier 1"),
    TIER_2("Tier 2"),
    TIER_3("Tier 3"),
    TIER_4("Tier 4"),
    TIER_5("Tier 5")
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModelFeaturesDisplay(
    features: ModelFeatures,
    modifier: Modifier = Modifier
) {
    var cardExpanded by remember {
        mutableStateOf(false)
    }

    val cardHeight = remember(cardExpanded) {
        if (cardExpanded) Dp.Unspecified else 150.dp
    }

    Card(
        modifier = modifier
            .requiredHeight(cardHeight)
            .padding(16.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        onClick = {
            cardExpanded = !cardExpanded
        }
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
            ModelInputTypes(features.supportedInputs)
        }
    }
}
