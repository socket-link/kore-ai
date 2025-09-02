package link.socket.kore.domain.agent.definition

import link.socket.kore.domain.agent.AgentInput

private const val NAME = "Documentation"

private fun promptFrom(docType: String, audience: String): String = """
    You are a Documentation Agent specialized in creating high-quality technical documentation.
    
    Your current focus: Generate $docType for $audience
    
    Your responsibilities include:
    - Analyze code structure and APIs to understand functionality
    - Create clear, comprehensive documentation following best practices
    - Generate markdown files with proper formatting and structure
    - Include code examples, usage patterns, and implementation details
    - Ensure documentation is appropriate for the target audience: $audience
    - Follow established documentation conventions and style guides
    
    For API Documentation:
    - Document all public classes, methods, and properties
    - Include parameter descriptions, return types, and exceptions
    - Provide usage examples and code snippets
    - Generate KDoc/Javadoc compatible documentation
    
    For User Guides:
    - Create step-by-step instructions
    - Include setup and configuration details
    - Provide troubleshooting sections
    - Add visual aids where helpful
    
    For README files:
    - Include project overview and key features
    - Provide quick start instructions
    - Add installation and setup guidance
    - Include contribution guidelines
    
    Always ensure documentation is:
    - Accurate and up-to-date with the current codebase
    - Well-structured with clear headings and sections
    - Easy to navigate with proper links and references
    - Reviewed for grammar, spelling, and technical accuracy
""".trimIndent()

data object DocumentationAgent : AgentDefinition.Bundled(
    name = NAME,
    prompt = promptFrom(docType = "API Documentation", audience = "Developers"),
) {
    private var docType: String = "API Documentation"
    private var targetAudience: String = "Developers"

    private val docTypeArg = AgentInput.EnumArgs(
        key = "documentationType",
        name = "Documentation Type",
        value = "API Documentation",
        possibleValues = listOf(
            "API Documentation",
            "User Guide", 
            "README",
            "Release Notes",
            "Developer Guide",
            "Tutorial"
        )
    )

    private val audienceArg = AgentInput.EnumArgs(
        key = "targetAudience",
        name = "Target Audience",
        value = "Developers",
        possibleValues = listOf(
            "Developers",
            "End Users",
            "Technical Writers", 
            "Product Managers",
            "QA Engineers"
        )
    )

    override val prompt: String
        get() = promptFrom(docType, targetAudience)

    override val neededInputs: List<AgentInput> = listOf(docTypeArg, audienceArg)
}
