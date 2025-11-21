package link.socket.kore.data

import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import link.socket.kore.agents.events.Database
import link.socket.kore.agents.events.EventRepository
import link.socket.kore.agents.events.messages.MessageRepository

val DEFAULT_JSON = Json {
    prettyPrint = false
    encodeDefaults = true
    classDiscriminator = "type"
    ignoreUnknownKeys = true
}

class RepositoryFactory(
    val scope: CoroutineScope,
    val driver: SqlDriver,
    val json: Json = DEFAULT_JSON,
) {
    val database: Database by lazy {
        Database(driver)
    }

    inline fun <reified T : Repository<*, *>> createRepository(): T = when (T::class) {
        EventRepository::class -> {
            EventRepository(json, scope, database) as T
        }
        MessageRepository::class -> {
            MessageRepository(json, scope, database) as T
        }
        UserConversationRepository::class -> {
            UserConversationRepository(json, scope) as T
        }
        else -> {
            throw IllegalArgumentException("No repository found for type ${T::class}")
        }
    }
}
