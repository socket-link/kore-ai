package link.socket.kore.agents.events.tickets

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import link.socket.kore.agents.core.AgentId
import link.socket.kore.agents.events.Database
import link.socket.kore.agents.tickets.TicketStore

/**
 * Sealed class representing errors that can occur during ticket operations.
 */
sealed class TicketError : Exception() {
    /**
     * Error when attempting an invalid state transition.
     */
    data class InvalidStateTransition(
        val fromState: TicketStatus,
        val toState: TicketStatus
    ) : TicketError() {
        override val message: String
            get() = "Invalid state transition: cannot transition from $fromState to $toState. " +
                "Valid transitions from $fromState are: ${fromState.validTransitions()}"
    }

    /**
     * Error when a ticket is not found.
     */
    data class TicketNotFound(
        val ticketId: TicketId
    ) : TicketError() {
        override val message: String
            get() = "Ticket not found: $ticketId"
    }

    /**
     * Error wrapping database exceptions.
     */
    data class DatabaseError(
        override val cause: Throwable
    ) : TicketError() {
        override val message: String
            get() = "Database error: ${cause.message}"
    }

    /**
     * Error for validation failures.
     */
    data class ValidationError(
        override val message: String
    ) : TicketError()
}

/**
 * Repository for persisting and querying Tickets using SQLDelight.
 *
 * This handles conversion between domain models and database representations,
 * with proper error handling and state transition validation.
 */
class TicketRepository(
    private val database: Database,
) {
    private val queries get() = database.ticketQueries

    /**
     * Create a new ticket in the database.
     *
     * @param ticket The ticket to create.
     * @return Result containing the created ticket or a TicketError.
     */
    suspend fun createTicket(ticket: Ticket): Result<Ticket> =
        withContext(Dispatchers.IO) {
            try {
                queries.insertTicket(
                    id = ticket.id,
                    title = ticket.title,
                    description = ticket.description,
                    ticket_type = ticket.type.name,
                    priority = ticket.priority.name,
                    status = ticket.status.name,
                    assigned_agent_id = ticket.assignedAgentId,
                    created_by_agent_id = ticket.createdByAgentId,
                    created_at = ticket.createdAt.toEpochMilliseconds(),
                    updated_at = ticket.updatedAt.toEpochMilliseconds(),
                    due_date = ticket.dueDate?.toEpochMilliseconds()
                )
                Result.success(ticket)
            } catch (e: Exception) {
                Result.failure(TicketError.DatabaseError(e))
            }
        }

    /**
     * Update the status of a ticket with state transition validation.
     *
     * @param ticketId The ID of the ticket to update.
     * @param newStatus The target status.
     * @return Result containing Unit on success or a TicketError.
     */
    suspend fun updateStatus(ticketId: TicketId, newStatus: TicketStatus): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                // First, get the current ticket to validate transition
                val currentTicket = getTicketInternal(ticketId)
                    ?: return@withContext Result.failure(TicketError.TicketNotFound(ticketId))

                // Validate the state transition
                if (!currentTicket.status.canTransitionTo(newStatus)) {
                    return@withContext Result.failure(
                        TicketError.InvalidStateTransition(
                            fromState = currentTicket.status,
                            toState = newStatus
                        )
                    )
                }

                // Perform the update
                val now = Clock.System.now().toEpochMilliseconds()
                queries.updateTicketStatus(
                    status = newStatus.name,
                    updated_at = now,
                    id = ticketId
                )

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(TicketError.DatabaseError(e))
            }
        }

    /**
     * Assign a ticket to an agent.
     *
     * @param ticketId The ID of the ticket to assign.
     * @param agentId The ID of the agent to assign to, or null to unassign.
     * @return Result containing Unit on success or a TicketError.
     */
    suspend fun assignTicket(ticketId: TicketId, agentId: AgentId?): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                // Verify ticket exists
                val ticket = getTicketInternal(ticketId)
                    ?: return@withContext Result.failure(TicketError.TicketNotFound(ticketId))

                val now = Clock.System.now().toEpochMilliseconds()
                queries.updateTicketAssignment(
                    assigned_agent_id = agentId,
                    updated_at = now,
                    id = ticketId
                )

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(TicketError.DatabaseError(e))
            }
        }

    /**
     * Retrieve a ticket by its ID.
     *
     * @param ticketId The ID of the ticket to retrieve.
     * @return Result containing the ticket (or null if not found) or a TicketError.
     */
    suspend fun getTicket(ticketId: TicketId): Result<Ticket?> =
        withContext(Dispatchers.IO) {
            try {
                val ticket = getTicketInternal(ticketId)
                Result.success(ticket)
            } catch (e: Exception) {
                Result.failure(TicketError.DatabaseError(e))
            }
        }

    /**
     * Get all tickets with a specific status.
     *
     * @param status The status to filter by.
     * @return Result containing the list of tickets or a TicketError.
     */
    suspend fun getTicketsByStatus(status: TicketStatus): Result<List<Ticket>> =
        withContext(Dispatchers.IO) {
            try {
                val tickets = queries.getTicketsByStatus(status.name)
                    .executeAsList()
                    .map { row -> mapRowToTicket(row) }
                Result.success(tickets)
            } catch (e: Exception) {
                Result.failure(TicketError.DatabaseError(e))
            }
        }

    /**
     * Get all tickets assigned to a specific agent.
     *
     * @param agentId The ID of the agent.
     * @return Result containing the list of tickets or a TicketError.
     */
    suspend fun getTicketsByAgent(agentId: AgentId): Result<List<Ticket>> =
        withContext(Dispatchers.IO) {
            try {
                val tickets = queries.getTicketsByAssignedAgent(agentId)
                    .executeAsList()
                    .map { row -> mapRowToTicket(row) }
                Result.success(tickets)
            } catch (e: Exception) {
                Result.failure(TicketError.DatabaseError(e))
            }
        }

    /**
     * Get all tickets.
     *
     * @return Result containing the list of all tickets or a TicketError.
     */
    suspend fun getAllTickets(): Result<List<Ticket>> =
        withContext(Dispatchers.IO) {
            try {
                val tickets = queries.getAllTickets()
                    .executeAsList()
                    .map { row -> mapRowToTicket(row) }
                Result.success(tickets)
            } catch (e: Exception) {
                Result.failure(TicketError.DatabaseError(e))
            }
        }

    /**
     * Get all tickets by priority.
     *
     * @param priority The priority to filter by.
     * @return Result containing the list of tickets or a TicketError.
     */
    suspend fun getTicketsByPriority(priority: TicketPriority): Result<List<Ticket>> =
        withContext(Dispatchers.IO) {
            try {
                val tickets = queries.getTicketsByPriority(priority.name)
                    .executeAsList()
                    .map { row -> mapRowToTicket(row) }
                Result.success(tickets)
            } catch (e: Exception) {
                Result.failure(TicketError.DatabaseError(e))
            }
        }

    /**
     * Get all tickets by type.
     *
     * @param type The type to filter by.
     * @return Result containing the list of tickets or a TicketError.
     */
    suspend fun getTicketsByType(type: TicketType): Result<List<Ticket>> =
        withContext(Dispatchers.IO) {
            try {
                val tickets = queries.getTicketsByType(type.name)
                    .executeAsList()
                    .map { row -> mapRowToTicket(row) }
                Result.success(tickets)
            } catch (e: Exception) {
                Result.failure(TicketError.DatabaseError(e))
            }
        }

    /**
     * Get all tickets created by a specific agent.
     *
     * @param agentId The ID of the creator agent.
     * @return Result containing the list of tickets or a TicketError.
     */
    suspend fun getTicketsByCreator(agentId: AgentId): Result<List<Ticket>> =
        withContext(Dispatchers.IO) {
            try {
                val tickets = queries.getTicketsByCreator(agentId)
                    .executeAsList()
                    .map { row -> mapRowToTicket(row) }
                Result.success(tickets)
            } catch (e: Exception) {
                Result.failure(TicketError.DatabaseError(e))
            }
        }

    /**
     * Update ticket details.
     *
     * @param ticketId The ID of the ticket to update.
     * @param title New title (optional).
     * @param description New description (optional).
     * @param priority New priority (optional).
     * @param dueDate New due date (optional).
     * @return Result containing the updated ticket or a TicketError.
     */
    suspend fun updateTicketDetails(
        ticketId: TicketId,
        title: String? = null,
        description: String? = null,
        priority: TicketPriority? = null,
        dueDate: Instant? = null
    ): Result<Ticket> =
        withContext(Dispatchers.IO) {
            try {
                // Get current ticket
                val currentTicket = getTicketInternal(ticketId)
                    ?: return@withContext Result.failure(TicketError.TicketNotFound(ticketId))

                val now = Clock.System.now().toEpochMilliseconds()
                queries.updateTicketDetails(
                    title = title ?: currentTicket.title,
                    description = description ?: currentTicket.description,
                    priority = (priority ?: currentTicket.priority).name,
                    due_date = (dueDate ?: currentTicket.dueDate)?.toEpochMilliseconds(),
                    updated_at = now,
                    id = ticketId
                )

                // Return updated ticket
                val updatedTicket = getTicketInternal(ticketId)
                    ?: return@withContext Result.failure(TicketError.TicketNotFound(ticketId))
                Result.success(updatedTicket)
            } catch (e: Exception) {
                Result.failure(TicketError.DatabaseError(e))
            }
        }

    /**
     * Delete a ticket.
     *
     * @param ticketId The ID of the ticket to delete.
     * @return Result containing Unit on success or a TicketError.
     */
    suspend fun deleteTicket(ticketId: TicketId): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                queries.deleteTicket(ticketId)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(TicketError.DatabaseError(e))
            }
        }

    // ==================== Private Helpers ====================

    /**
     * Internal helper to get a ticket without wrapping in Result.
     */
    private fun getTicketInternal(ticketId: TicketId): Ticket? {
        val row = queries.getTicketById(ticketId).executeAsOneOrNull()
            ?: return null
        return mapRowToTicket(row)
    }

    /**
     * Map a database row to a Ticket domain object.
     */
    private fun mapRowToTicket(row: TicketStore): Ticket {
        return Ticket(
            id = row.id,
            title = row.title,
            description = row.description,
            type = TicketType.valueOf(row.ticket_type),
            priority = TicketPriority.valueOf(row.priority),
            status = TicketStatus.valueOf(row.status),
            assignedAgentId = row.assigned_agent_id,
            createdByAgentId = row.created_by_agent_id,
            createdAt = Instant.fromEpochMilliseconds(row.created_at),
            updatedAt = Instant.fromEpochMilliseconds(row.updated_at),
            dueDate = row.due_date?.let { Instant.fromEpochMilliseconds(it) }
        )
    }
}
