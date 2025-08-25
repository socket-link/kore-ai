package link.socket.kore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import java.awt.Dimension
import link.socket.kore.domain.model.llm.AI_Configuration
import link.socket.kore.domain.model.llm.DEFAULT_AI_CONFIGURATION

fun main() =
    application {
        val selectedClientProvider: AI_Configuration<*, *> by remember {
            mutableStateOf(DEFAULT_AI_CONFIGURATION)
        }

        Window(
            onCloseRequest = ::exitApplication,
            title = "KoreAI",
            state =
                WindowState(
                    width = 800.dp,
                    height = 1200.dp,
                ),
        ) {
            window.minimumSize = Dimension(600, 800)

            MainView(
                clientProvider = selectedClientProvider,
            )
        }
    }
