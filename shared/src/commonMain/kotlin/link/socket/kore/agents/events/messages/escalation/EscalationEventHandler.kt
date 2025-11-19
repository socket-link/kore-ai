package link.socket.kore.agents.events.messages.escalation

import link.socket.kore.agents.events.EventHandler
import link.socket.kore.agents.events.MessageEvent
import link.socket.kore.agents.events.Subscription

/**
 * Listens for `MessageEvent.EscalationRequested` and notifies a human.
 * Uses AgentMessageApi to read the thread for any additional checks.
 */
class EscalationEventHandler(
    private val humanNotifier: Notifier.Human,
) : EventHandler<MessageEvent.EscalationRequested, Subscription>() {

    // Subscribe to EscalationRequested events and send them to Human
    override suspend fun invoke(
        event: MessageEvent.EscalationRequested,
        subscription: Subscription?,
    ) {
        super.invoke(event, subscription)
        notifyHumanEscalation(event)
    }

    private suspend fun notifyHumanEscalation(
        event: MessageEvent.EscalationRequested,
    ) {
        // By design, status should already be WAITING_FOR_HUMAN after escalateToHuman()
        // We rely on AgentMessageApi to enforce the transition; here we just notify.
        humanNotifier.notifyEscalation(
            threadId = event.threadId,
            agentId = event.eventSource.getIdentifier(),
            reason = event.reason,
            context = event.context,
        )
    }
}
