package link.socket.kore.agents

import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import link.socket.kore.agents.core.MessageSeverity
import link.socket.kore.agents.implementations.CodeWriterAgent
import link.socket.kore.agents.tools.AskHumanTool
import link.socket.kore.agents.tools.ReadCodebaseTool
import link.socket.kore.agents.tools.RunTestsTool
import link.socket.kore.agents.tools.WriteCodeFileTool

class AgentSystemIntegrationTest {
    @Test
    fun `complete workflow with CodeWriterAgent`() = runBlocking {
        val tempDir = Files.createTempDirectory("agent_test").toFile()
        try {
            val tools = mapOf(
                "ask_human" to AskHumanTool { question -> "Approved: $question" },
                "write_code_file" to WriteCodeFileTool(tempDir.absolutePath),
                "read_codebase" to ReadCodebaseTool(tempDir.absolutePath),
                "run_tests" to RunTestsTool(tempDir.absolutePath)
            )

            val agent = CodeWriterAgent(tools)

            // Set task
            agent.setTask("Add a new tool called TestTool")

            // Agent workflow
            val context = agent.perceive()
            assertTrue(context.currentState["task"] == "Add a new tool called TestTool")

            val plan = agent.plan()
            assertTrue(plan.steps.isNotEmpty())
            assertTrue(plan.requiresHumanApproval)

            val signal = agent.signal()
            assertNotNull(signal)
            assertEquals(MessageSeverity.QUESTION, signal?.severity)

            val outcome = agent.act()
            assertTrue(outcome.success)
        } finally {
            tempDir.deleteRecursively()
        }
    }
}
