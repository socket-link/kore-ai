package link.socket.kore.ui.model

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PerformanceChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val backgroundColor = remember(color) {
        color.copy(alpha = 0.1f)
    }

    val borderColor = remember(color) {
        color.copy(alpha = 0.3f)
    }

    val textColor = remember(color) {
        color.copy(alpha = 0.7f)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption,
            color = textColor,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .background(
                    color = backgroundColor,
                    shape = RoundedCornerShape(16.dp),
                )
                .border(
                    BorderStroke(
                        width = 1.dp,
                        color = borderColor,
                    ),
                    RoundedCornerShape(16.dp),
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
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
