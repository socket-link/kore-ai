package link.socket.kore.domain.agent

import kotlinx.coroutines.CoroutineScope
import link.socket.kore.data.ConversationRepository
import link.socket.kore.domain.agent.definition.AgentDefinition
import link.socket.kore.domain.model.ai.configuration.AI_Configuration

object AgentProvider {

    fun createAgent(
        scope: CoroutineScope,
        config: AI_Configuration,
        definition: AgentDefinition,
        conversationRepository: ConversationRepository,
    ): KoreAgent = KoreAgent(
        scope = scope,
        definition = definition,
        conversationRepository = conversationRepository,
        config = config,
    )
}
