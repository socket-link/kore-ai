package link.socket.kore.agents.tools

expect class RunTestsToolTest {
    fun `validateParameters allows optional testPath`()
    fun `execute runs tests and captures output success`()
    fun `execute passes test filter when provided`()
    fun `execute handles failure gracefully`()
}
