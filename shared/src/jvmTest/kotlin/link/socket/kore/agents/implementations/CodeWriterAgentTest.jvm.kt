package link.socket.kore.agents.implementations

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

actual class CodeWriterAgentTest {

    @Test
    actual fun `setTask updates internal state via perceive`() {
        val agent = CodeWriterAgent(tools = emptyMap())
        // Initial perceive
        val before = agent.perceive()
        assertEquals("none", before.currentState["task"])
        assertEquals(false, before.currentState["planExists"])
        assertEquals(emptyList<String>(), before.currentState["executionHistory"])

        agent.setTask("Add a new tool called ExampleTool")
        val after = agent.perceive()
        assertEquals("Add a new tool called ExampleTool", after.currentState["task"])
        assertEquals(false, after.currentState["planExists"])
        assertEquals(emptyList<String>(), after.currentState["executionHistory"])
    }

    @Test
    actual fun `perceive returns Context with task info`() {
        val agent = CodeWriterAgent(emptyMap())
        agent.setTask("Do something trivial")
        val ctx = agent.perceive()
        assertEquals("Do something trivial", ctx.currentState["task"])
        assertEquals(false, ctx.currentState["planExists"])
        @Suppress("UNCHECKED_CAST")
        val history = ctx.currentState["executionHistory"] as List<String>
        assertEquals(true, history.isEmpty())
    }

    @Test
    actual fun `reason generates appropriate plan for known task pattern`() {
        val agent = CodeWriterAgent(emptyMap())
        agent.setTask("Add a new tool called EchoTool")
        val plan = agent.reason()
        assertEquals(true, plan.requiresHumanApproval)
        assertEquals(2, plan.estimatedComplexity)
        assertEquals(
            listOf(
                "Read existing tool implementations",
                "Generate new tool code",
                "Write tool to file",
                "Ask human for validation"
            ),
            plan.steps
        )
    }

    @Test
    actual fun `reason falls back to clarification for unknown task`() {
        val agent = CodeWriterAgent(emptyMap())
        agent.setTask("Mysterious task")
        val plan = agent.reason()
        assertEquals(true, plan.requiresHumanApproval)
        assertEquals(1, plan.estimatedComplexity)
        assertEquals(listOf("Ask human for clarification"), plan.steps)
    }

    @Test
    actual fun `signal returns message when approval needed`() {
        val agent = CodeWriterAgent(emptyMap())
        agent.setTask("Add a new tool called EchoTool")
        agent.plan()
        val message = agent.signal()
        assertNotNull(message)
        assertEquals(true, message.content.contains("Plan requires approval"))
        assertEquals(true, message.requiresResponse)
    }

    @Test
    actual fun `full workflow executes steps then completes`() {
        val agent = CodeWriterAgent(emptyMap())
        agent.setTask("Add a new tool called EchoTool")

        val plan = agent.plan()
        assertEquals(true, plan.requiresHumanApproval)

        // Execute through all steps
        for (i in plan.steps.indices) {
            val outcome = agent.act()
            assertEquals(true, outcome.success)
            assertNotNull(outcome.result)
            assertEquals(true, (outcome.result as String).contains("Executed:"))
        }

        // After finishing steps, next act() reports completion
        val done = agent.act()
        assertEquals(true, done.success)
        assertEquals("Plan completed", done.result)

        // Perceive should now show execution history size equals steps size
        val ctx = agent.perceive()
        @Suppress("UNCHECKED_CAST")
        val history = ctx.currentState["executionHistory"] as List<String>
        assertEquals(plan.steps.size, history.size)
    }
}
