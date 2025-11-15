package link.socket.kore.agents.core

/**
 * Contract for minimal autonomous agents.
 * All return types are immutable data classes defined in AgentTypes.kt.
 * This interface is platform-agnostic (commonMain) for KMP.
 */
interface MinimalAutonomousAgent {

    /**
     * Reads and interprets current state
     */
    fun perceive(): Context

    /**
     * Decides what to do based on perceived context
     */
    fun reason(): Plan

    /**
     * Executes one atomic action from the plan
     */
    fun act(): Outcome

    /**
     * Communicates when stuck or uncertain
     */
    fun signal(): Message?

    /**
     * Breaks down complex task into smaller steps
     */
    fun plan(): Plan
}
