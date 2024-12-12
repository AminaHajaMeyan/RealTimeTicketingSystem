package com.amina.backend.ticket;

import com.amina.backend.websocket.ActivityWebSocketHandler;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Manages the ticketing system, serving as a shared resource for vendors and customers.
 * <p>
 * The {@code TicketPool} class implements concurrency control to ensure thread-safe operations
 * when multiple threads (vendors and customers) interact with the pool.
 * </p>
 *
 * @author Amina
 * @version 1.0
 * @since 2024-12-11
 */
public class TicketPool {
    private final ConcurrentLinkedQueue<Ticket> tickets = new ConcurrentLinkedQueue<>();
    private final int maxTicketCapacity;
    private final int totalTickets;
    private int totalTicketsSold = 0;
    private boolean isTerminated = false;
    private final ActivityWebSocketHandler webSocketHandler;

    /**
     * Constructs a {@code TicketPool} with specified capacity, total tickets, and WebSocket handler.
     *
     * @param maxTicketCapacity The maximum capacity of tickets in the pool.
     * @param totalTickets      The total number of tickets to be sold.
     * @param webSocketHandler  The WebSocket handler for broadcasting real-time updates.
     */
    public TicketPool(int maxTicketCapacity, int totalTickets, ActivityWebSocketHandler webSocketHandler) {
        this.maxTicketCapacity = maxTicketCapacity;
        this.totalTickets = totalTickets;
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * Adds a ticket to the pool if the maximum capacity and total ticket limits are not exceeded.
     *
     * @param ticket   The ticket to add.
     * @param vendorId The ID of the vendor adding the ticket.
     * @return {@code true} if the ticket was successfully added, {@code false} otherwise.
     */
    public synchronized boolean addTicket(Ticket ticket, int vendorId) {
        if (totalTicketsSold + tickets.size() >= totalTickets) {
            System.out.println("Vendor-" + vendorId + ": Cannot add more tickets. Total tickets limit reached.");
            return false;
        }

        if (tickets.size() >= maxTicketCapacity) {
            System.out.println("Vendor-" + vendorId + ": Pool is full. Waiting to add more tickets.");
            return false;
        }

        tickets.add(ticket);
        notifyAll(); // Notify waiting customers
        webSocketHandler.broadcastMessage(String.format("Vendor-%d added %s", vendorId, ticket));
        broadcastTicketStatus();
        return true;

    }

    /**
     * Removes a ticket from the pool for a customer.
     * <p>
     * Updates the total tickets sold and broadcasts the event via WebSocket.
     * </p>
     *
     * @param customerId The ID of the customer retrieving the ticket.
     * @return The retrieved ticket, or {@code null} if no ticket is available or the system is terminated.
     */
    public synchronized Ticket removeTicket(int customerId) {
        if (isTerminated()) {
            return null;
        }

        Ticket ticket = tickets.poll();
        if (ticket != null) {
            totalTicketsSold++;
            if (totalTicketsSold > totalTickets) {
                totalTicketsSold--; // Revert the increment if it exceeds the limit
                System.out.println("Error: Total tickets sold exceeds the configured totalTickets.");
                return null;
            }
            webSocketHandler.broadcastMessage(String.format(
                    "Customer-%d successfully retrieved %s. Total Tickets Sold: %d/%d. Remaining Tickets: %d",
                    customerId, ticket, totalTicketsSold, totalTickets, getRemainingTickets()));
            broadcastTicketStatus();
        }
        return ticket;
    }

    /**
     * Marks the system as terminated, preventing further operations.
     */
    public synchronized void stopSystem() {
        isTerminated = true;
        notifyAll(); // Notify all waiting threads
    }

    /**
     * Checks if the system is terminated based on the total tickets sold and pool state.
     *
     * @return {@code true} if the system is terminated, {@code false} otherwise.
     */
    public synchronized boolean isTerminated() {
        if (totalTicketsSold >= totalTickets && tickets.isEmpty()) {
            isTerminated = true;
        }
        return isTerminated;
    }

    /**
     * Gets the number of tickets remaining in the system.
     *
     * @return The number of tickets remaining.
     */
    public synchronized int getRemainingTickets() {
        return totalTickets - totalTicketsSold;
    }

    /**
     * Gets the total number of tickets sold.
     *
     * @return The total tickets sold.
     */
    public synchronized int getTotalTicketsSold() {
        return totalTicketsSold;
    }

    /**
     * Gets the number of tickets currently in the pool.
     *
     * @return The number of tickets in the pool.
     */
    public synchronized int getTicketsInPool() {
        return tickets.size();
    }

    /**
     * Generates a summary of the system's state, including the number of tickets sold,
     * tickets remaining, and whether the system was stopped manually or naturally.
     *
     * @param manuallyStopped {@code true} if the system was manually stopped, {@code false} otherwise.
     * @return A summary string.
     */
    public synchronized String generateSummary(boolean manuallyStopped) {
        return String.format("Summary: Total Tickets Sold: %d/%d, Tickets Remaining: %d. System was %s.",
                totalTicketsSold, totalTickets, getRemainingTickets(),
                manuallyStopped ? "manually stopped" : "naturally terminated");
    }

    private void broadcastTicketStatus() {
        int totalTickets = this.totalTickets;
        int ticketsSold = this.totalTicketsSold;
        int remainingTickets = getRemainingTickets();
        String message = String.format(
                "{\"totalTickets\": %d, \"ticketsSold\": %d, \"remainingTickets\": %d}",
                totalTickets, ticketsSold, remainingTickets
        );
        webSocketHandler.broadcastMessage(message);
    }
    private void broadcastTransactionData(String type, int amount) {
        String message = String.format(
                "{\"type\": \"%s\", \"amount\": %d, \"timestamp\": \"%s\"}",
                type, amount, java.time.Instant.now().toString()
        );
        webSocketHandler.broadcastMessage(message); // Broadcast message to WebSocket clients
    }


}
