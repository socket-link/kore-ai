package link.socket.kore.agents.events.meetings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.Clock
import link.socket.kore.agents.core.AssignedTo
import link.socket.kore.agents.events.Event
import link.socket.kore.agents.events.EventSource
import link.socket.kore.agents.events.MeetingEvents
import link.socket.kore.agents.events.meetings.AgendaItem
import link.socket.kore.agents.events.meetings.Meeting
import link.socket.kore.agents.events.meetings.MeetingInvitation
import link.socket.kore.agents.events.meetings.MeetingOutcome
import link.socket.kore.agents.events.meetings.MeetingStatus
import link.socket.kore.agents.events.meetings.MeetingType

class MeetingEventsTest {

    private val stubEventSource = EventSource.Agent("agent-alpha")
    private val stubEventSource2 = EventSource.Agent("agent-beta")

    private fun sampleMeeting(): Meeting {
        val invitation = MeetingInvitation(
            title = "Daily Standup",
            agenda = listOf(
                AgendaItem(id = "ai-1", topic = "Yesterday"),
                AgendaItem(id = "ai-2", topic = "Today"),
            ),
            requiredParticipants = listOf(AssignedTo.Agent("agent-1")),
        )
        return Meeting(
            id = "m-1",
            type = MeetingType.AdHoc("sync"),
            status = MeetingStatus.Scheduled(Clock.System.now()),
            invitation = invitation,
        )
    }

    @Test
    fun `can construct all meeting event samples`() {
        val now = Clock.System.now()
        val meeting = sampleMeeting()

        val scheduled: Event = MeetingEvents.MeetingScheduled(
            eventId = "e-1",
            meeting = meeting,
            scheduledBy = stubEventSource,
        )

        val started: Event = MeetingEvents.MeetingStarted(
            eventId = "e-2",
            meetingId = meeting.id,
            threadId = "thread-1",
            startedAt = now,
            startedBy = stubEventSource2,
        )

        val itemStarted: Event = MeetingEvents.AgendaItemStarted(
            eventId = "e-3",
            meetingId = meeting.id,
            agendaItem = meeting.invitation.agenda.first(),
            timestamp = now,
            startedBy = stubEventSource2,
        )

        val itemCompleted: Event = MeetingEvents.AgendaItemCompleted(
            eventId = "e-4",
            meetingId = meeting.id,
            agendaItemId = meeting.invitation.agenda.first().id,
            completedAt = now,
            completedBy = stubEventSource,
        )

        val completed: Event = MeetingEvents.MeetingCompleted(
            eventId = "e-5",
            meetingId = meeting.id,
            outcomes = listOf(
                MeetingOutcome.DecisionMade("mo-1", "Ship feature", EventSource.Agent("agent-1"))
            ),
            completedAt = now,
            completedBy = stubEventSource,
        )

        val cancelled: Event = MeetingEvents.MeetingCanceled(
            eventId = "e-6",
            meetingId = meeting.id,
            reason = "No quorum",
            canceledAt = now,
            canceledBy = stubEventSource,
        )

        // Basic assertions that eventClassType discriminators are set
        assertEquals(MeetingEvents.MeetingScheduled.EVENT_CLASS_TYPE, scheduled.eventClassType)
        assertEquals(MeetingEvents.MeetingStarted.EVENT_CLASS_TYPE, started.eventClassType)
        assertEquals(MeetingEvents.AgendaItemStarted.EVENT_CLASS_TYPE, itemStarted.eventClassType)
        assertEquals(MeetingEvents.AgendaItemCompleted.EVENT_CLASS_TYPE, itemCompleted.eventClassType)
        assertEquals(MeetingEvents.MeetingCompleted.EVENT_CLASS_TYPE, completed.eventClassType)
        assertEquals(MeetingEvents.MeetingCanceled.EVENT_CLASS_TYPE, cancelled.eventClassType)
    }
}
