package link.socket.kore.ui.model

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.util.date.*
import link.socket.kore.domain.limits.ModelLimits

@Composable
fun ModelLimitsDisplay(
    limits: ModelLimits,
    trainingCutoffDate: GMTDate,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        limits.let { limits ->
            ModelTokenLimits(limits.token)
            Spacer(modifier = Modifier.height(16.dp))
            ModelRateLimits(limits.rate)
        }

        ModelTrainingCutoffLimit(trainingCutoffDate)
    }
}
