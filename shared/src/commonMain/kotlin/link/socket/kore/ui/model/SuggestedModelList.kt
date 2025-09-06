package link.socket.kore.ui.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import link.socket.kore.domain.model.llm.AI_Provider
import link.socket.kore.domain.model.llm.LLM
import link.socket.kore.ui.theme.themeColors

@Composable
fun SuggestedModelList(
    selectedModelId: String,
    suggestedModels: List<Pair<AI_Provider<*, *>, LLM<*>>>?,
    onModelSelected: (LLM<*>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Suggested Models",
            style = MaterialTheme.typography.subtitle2,
            fontWeight = MaterialTheme.typography.subtitle2.fontWeight,
        )
        Spacer(modifier = Modifier.requiredHeight(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            items(suggestedModels ?: emptyList()) { (provider, model) ->
                val isSelected = remember(selectedModelId) {
                    model.name == selectedModelId

                }

                SuggestedModelCard(
                    modifier = Modifier
                        .requiredHeight(320.dp)
                        .requiredWidth(240.dp),
                    provider = provider,
                    model = model,
                    isSelected = isSelected,
                    onClick = {
                        onModelSelected(model)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SuggestedModelCard(
    provider: AI_Provider<*, *>,
    model: LLM<*>,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val themeColors = themeColors()
    val color = remember(isSelected) {
        if (isSelected) {
            themeColors.primary
        } else {
            themeColors.surface
        }
    }

    Card(
        modifier = modifier,
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = color,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp,
                )
        ) {
            Text(
                text = provider.name,
                style = MaterialTheme.typography.subtitle2,
            )
            Spacer(modifier = Modifier.requiredHeight(8.dp))
            Text(
                text = model.displayName,
                style = MaterialTheme.typography.subtitle1,
            )
            Spacer(modifier = Modifier.requiredHeight(8.dp))
            Text(
                text = model.description,
                style = MaterialTheme.typography.caption,
            )
        }
    }
}
