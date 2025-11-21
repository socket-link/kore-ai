package link.socket.kore.agents.events.meetings

import link.socket.kore.agents.core.AgentId
import link.socket.kore.agents.events.utils.ConsoleEventLogger
import link.socket.kore.agents.events.utils.EventLogger

class AgentMeetingsApiFactory(
    private val meetingBuilder: MeetingBuilder,
    private val meetingOrchestrator: MeetingOrchestrator,
    private val logger: EventLogger = ConsoleEventLogger(),
) {

    /** Create an [AgentMeetingsApi] for the given [agentId]. */
    fun create(agentId: AgentId): AgentMeetingsApi = AgentMeetingsApi(
        agentId = agentId,
        meetingBuilder = meetingBuilder,
        meetingOrchestrator = meetingOrchestrator,
        logger = logger,
    )
}
