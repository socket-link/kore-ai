package link.socket.kore.agents.implementations

expect class CodeWriterAgentTest {
    fun `setTask updates internal state via perceive`()
    fun `perceive returns Context with task info`()
    fun `reason generates appropriate plan for known task pattern`()
    fun `reason falls back to clarification for unknown task`()
    fun `signal returns message when approval needed`()
    fun `full workflow executes steps then completes`()
}
