package link.socket.kore.ui.model

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.util.date.GMTDate
import link.socket.kore.domain.limits.ModelLimits

@Composable
fun ModelLimitsSection(
    limits: ModelLimits,
    trainingCutoffDate: GMTDate,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        limits.let { limits ->
            ModelTokenInfo(limits.token)

            Spacer(modifier = Modifier.height(16.dp))

            ModelRateLimitsSection(limits.rate)
        }

        ModelTrainingCutoffInfo(trainingCutoffDate)
    }
}
