package link.socket.kore.domain.koog

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleAnthropicExecutor
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import link.socket.kore.domain.agent.KoreAgent
import link.socket.kore.domain.ai.AI_Anthropic
import link.socket.kore.domain.ai.AI_Google
import link.socket.kore.domain.ai.AI_OpenAI
import link.socket.kore.domain.config.AI_Configuration
import link.socket.kore.domain.llm.toKoogLLMModel

class KoogAgentFactory() {

    fun createKoogAgent(
        aiConfiguration: AI_Configuration,
        agent: KoreAgent,
    ): AIAgent<String, *> {
        val executor = when (val ai = aiConfiguration.aiProvider) {
            is AI_Anthropic -> simpleAnthropicExecutor(ai.apiToken)
            is AI_Google -> simpleGoogleAIExecutor(ai.apiToken)
            is AI_OpenAI -> simpleOpenAIExecutor(ai.apiToken)
        }
        return AIAgent(
            executor = executor,
            systemPrompt = agent.prompt,
            llmModel = aiConfiguration.selectedLLM?.toKoogLLMModel() ?: GoogleModels.Gemini2_5Flash,
            temperature = 0.7,
        )
    }
}
