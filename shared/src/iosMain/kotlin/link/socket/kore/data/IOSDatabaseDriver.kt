package link.socket.kore.data

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import link.socket.kore.agents.events.Database

/** Creates a Native SQLDelight driver for the given database on iOS. */
fun createIosDriver(
    dbName: String = "kore.db",
) = NativeSqliteDriver(Database.Schema, dbName)
