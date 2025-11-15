package link.socket.kore.agents.tools

expect class WriteCodeFileToolTest {
    fun `validateParameters enforces filePath and content strings`()
    fun `execute writes file with content and creates parent directories`()
    fun `execute fails gracefully on invalid path`()
}
