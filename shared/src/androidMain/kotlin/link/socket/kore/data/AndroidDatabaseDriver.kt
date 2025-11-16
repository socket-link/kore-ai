package link.socket.kore.data

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import link.socket.kore.agents.events.Database

/** Creates a SQLDelight Android driver for the given database on Android. */
fun createAndroidDriver(
    context: Context,
    dbName: String = "kore.db",
) = AndroidSqliteDriver(Database.Schema, context, dbName)
