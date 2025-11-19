package link.socket.kore.agents.events

import java.util.UUID
import link.socket.kore.agents.core.AgentId

actual fun generateEventId(agentId: AgentId): String =
    UUID.randomUUID().toString() + "/$agentId"
