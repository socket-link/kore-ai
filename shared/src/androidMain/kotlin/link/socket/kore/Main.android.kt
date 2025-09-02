package link.socket.kore

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import link.socket.kore.domain.model.llm.AI_Configuration
import link.socket.kore.domain.model.llm.DEFAULT_AI_CONFIGURATION
import link.socket.kore.ui.App

@Composable
fun MainView(
    clientProvider: AI_Configuration<*, *>,
) = App(clientProvider)

@Preview
@Composable
fun MainViewPreview() {
    MainView(
        clientProvider = DEFAULT_AI_CONFIGURATION,
    )
}
