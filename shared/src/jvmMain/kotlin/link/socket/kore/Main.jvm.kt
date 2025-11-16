package link.socket.kore

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.cash.sqldelight.db.SqlDriver
import link.socket.kore.data.createJvmDriver
import link.socket.kore.ui.App

@Composable
fun MainView() {
    val databaseDriver: SqlDriver = remember {
        createJvmDriver()
    }

    App(
        modifier = Modifier
            .fillMaxSize(),
        databaseDriver = databaseDriver,
    )
}

@Preview
@Composable
fun MainViewPreview() {
    MainView()
}
