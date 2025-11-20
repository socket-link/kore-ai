package link.socket.kore.agents.events

import java.util.UUID
import link.socket.kore.agents.core.AgentId

actual fun generateEventId(agentId: AgentId): EventId =
    UUID.randomUUID().toString()
