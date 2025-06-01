package link.socket.kore.model

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import kotlin.time.Duration.Companion.hours
import link.kore.shared.config.KotlinConfig

val OPEN_AI =
    OpenAI(
        token = KotlinConfig.openai_api_key,
        timeout = Timeout(socket = 1.hours),
        logging =
            LoggingConfig(
                logLevel = LogLevel.None,
                logger = Logger.Simple,
            ),
    )
