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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import link.socket.kore.domain.ai.model.AIModelFeatures

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModelFeaturesSection(
    features: AIModelFeatures,
    modifier: Modifier = Modifier,
) {
    val cardExpanded: MutableState<Boolean> = remember {
        mutableStateOf(false)
    }

    val cardHeight: Dp = remember(cardExpanded) {
        if (cardExpanded.value) {
            Dp.Unspecified
        } else {
            150.dp
        }
    }

    Card(
        modifier = modifier
            .requiredHeight(cardHeight)
            .padding(16.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        onClick = {
            cardExpanded.value = !cardExpanded.value
        },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                style = MaterialTheme.typography.h6,
                text = "Use Cases",
            )

            ModelSupportedInputsSection(features.supportedInputs)
        }
    }
}
