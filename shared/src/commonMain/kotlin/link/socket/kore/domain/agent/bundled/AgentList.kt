package link.socket.kore.domain.agent.bundled

import link.socket.kore.domain.agent.AgentDefinition
import link.socket.kore.domain.agent.AgentInput
import link.socket.kore.domain.agent.bundled.code.APIDesignAgent
import link.socket.kore.domain.agent.bundled.code.CleanJsonAgent
import link.socket.kore.domain.agent.bundled.code.DocumentationAgent
import link.socket.kore.domain.agent.bundled.code.PerformanceOptimizationAgent
import link.socket.kore.domain.agent.bundled.code.PlatformCompatibilityAgent
import link.socket.kore.domain.agent.bundled.code.QATestingAgent
import link.socket.kore.domain.agent.bundled.code.ReleaseManagementAgent
import link.socket.kore.domain.agent.bundled.code.SecurityReviewAgent
import link.socket.kore.domain.agent.bundled.code.WriteCodeAgent
import link.socket.kore.domain.agent.bundled.general.BusinessAgent
import link.socket.kore.domain.agent.bundled.general.CareerAgent
import link.socket.kore.domain.agent.bundled.general.CookingAgent
import link.socket.kore.domain.agent.bundled.general.DIYAgent
import link.socket.kore.domain.agent.bundled.general.EmailAgent
import link.socket.kore.domain.agent.bundled.general.FinancialAgent
import link.socket.kore.domain.agent.bundled.general.HealthAgent
import link.socket.kore.domain.agent.bundled.general.LanguageAgent
import link.socket.kore.domain.agent.bundled.general.MediaAgent
import link.socket.kore.domain.agent.bundled.general.StudyAgent
import link.socket.kore.domain.agent.bundled.general.TechAgent
import link.socket.kore.domain.agent.bundled.general.TravelAgent
import link.socket.kore.domain.agent.bundled.prompt.ComparePromptsAgent
import link.socket.kore.domain.agent.bundled.prompt.TestAgentAgent
import link.socket.kore.domain.agent.bundled.prompt.WritePromptAgent
import link.socket.kore.domain.agent.bundled.reasoning.DelegateTasksAgent
import link.socket.kore.domain.agent.bundled.reasoning.ReActAgent

val codeAgents =
    listOf(
        APIDesignAgent,
        CleanJsonAgent,
        DocumentationAgent,
        PerformanceOptimizationAgent,
        PlatformCompatibilityAgent,
        QATestingAgent,
        ReleaseManagementAgent,
        SecurityReviewAgent,
        WriteCodeAgent,
    )

val generalAgents =
    listOf(
        BusinessAgent,
        CareerAgent,
        CookingAgent,
        DIYAgent,
        EmailAgent,
        FinancialAgent,
        HealthAgent,
        LanguageAgent,
        MediaAgent,
        StudyAgent,
        TechAgent,
        TravelAgent,
    )

val promptAgents =
    listOf(
        ComparePromptsAgent,
        TestAgentAgent(""),
        WritePromptAgent,
    )

val reasoningAgents =
    listOf(
        DelegateTasksAgent,
        ReActAgent,
    )

val agentList: List<AgentDefinition> =
    listOf(
        *codeAgents.toTypedArray(),
        *generalAgents.toTypedArray(),
        *promptAgents.toTypedArray(),
        *reasoningAgents.toTypedArray(),
    )

val agentArgsList: List<String> =
    agentList.map { agent ->
        "${agent.name}(" + (
            agent.neededInputs.joinToString(", ") { input ->
                input.key + ": " +
                    when (input) {
                        is AgentInput.EnumArgs -> "Enum"
                        is AgentInput.StringArg -> "String"
                        is AgentInput.ListArg -> "List<String>"
                    }
            }
            ) + ")"
    }

fun String?.getAgentDefinition(prompt: String?): AgentDefinition =
    when (this) {
        APIDesignAgent.name -> APIDesignAgent
        BusinessAgent.name -> BusinessAgent
        CareerAgent.name -> CareerAgent
        CleanJsonAgent.name -> CleanJsonAgent
        ComparePromptsAgent.name -> ComparePromptsAgent
        CookingAgent.name -> CookingAgent
        DelegateTasksAgent.name -> DelegateTasksAgent
        DIYAgent.name -> DIYAgent
        DocumentationAgent.name -> DocumentationAgent
        EmailAgent.name -> EmailAgent
        FinancialAgent.name -> FinancialAgent
        HealthAgent.name -> HealthAgent
        LanguageAgent.name -> LanguageAgent
        MediaAgent.name -> MediaAgent
        PerformanceOptimizationAgent.name -> PerformanceOptimizationAgent
        PlatformCompatibilityAgent.name -> PlatformCompatibilityAgent
        QATestingAgent.name -> QATestingAgent
        ReActAgent.name -> ReActAgent
        ReleaseManagementAgent.name -> ReleaseManagementAgent
        SecurityReviewAgent.name -> SecurityReviewAgent
        StudyAgent.name -> StudyAgent
        TechAgent.name -> TechAgent
        TravelAgent.name -> TravelAgent
        WriteCodeAgent.name -> WriteCodeAgent
        WritePromptAgent.name -> WritePromptAgent
        else -> TestAgentAgent(prompt.orEmpty())
    }
