package link.socket.kore.ui.model

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import link.socket.kore.domain.model.tool.ProvidedTool

@Composable
fun ModelTools(tools: List<ProvidedTool<*>>) {
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
