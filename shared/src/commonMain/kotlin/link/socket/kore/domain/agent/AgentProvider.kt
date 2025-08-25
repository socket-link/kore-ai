package link.socket.kore.domain.agent

import kotlinx.coroutines.CoroutineScope
import link.socket.kore.data.ConversationRepository
import link.socket.kore.domain.model.llm.AI_Configuration

object AgentProvider {

    private val DEFAULT_INPUTS = emptyMap<String, AgentInput>()
    
    fun createAgent(
        scope: CoroutineScope,
        definition: AgentDefinition,
        conversationRepository: ConversationRepository,
        config: AI_Configuration<*, *>,
        inputs: Map<String, AgentInput> = DEFAULT_INPUTS,
    ): KoreAgent {
        definition.parseInputs(inputs)

        return KoreAgent(
            scope = scope,
            config = config,
            definition = definition,
            conversationRepository = conversationRepository,
        )
    }
}
