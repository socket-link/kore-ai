package link.socket.kore.ui.model

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import link.socket.kore.domain.model.llm.ModelFeatures

@Composable
fun ModelInputTypes(supportedInputs: ModelFeatures.SupportedInputs) {
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
