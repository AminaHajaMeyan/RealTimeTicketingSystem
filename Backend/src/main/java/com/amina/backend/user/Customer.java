package com.amina.backend.user;

import com.amina.backend.ticket.Ticket;
import com.amina.backend.ticket.TicketPool;

import java.util.Random;

/**
 * Represents a customer in the ticketing system.
 * <p>
 * A {@code Customer} interacts with the shared {@code TicketPool}, attempting to purchase tickets.
 * Each customer runs as a separate thread, simulating real-world asynchronous behavior.
 * </p>
 *
 * @author Amina
 * @version 1.0
 * @since 2024-12-11
 */
public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int customerId;
    private final Random random = new Random();

    /**
     * Constructs a new {@code Customer} with the specified {@code TicketPool} and ID.
     *
     * @param ticketPool The shared ticket pool.
     * @param customerId The unique ID of the customer.
     */
    public Customer(TicketPool ticketPool, int customerId) {
        this.ticketPool = ticketPool;
        this.customerId = customerId;
    }

    /**
     * The main logic for the customer's thread.
     * <p>
     * The customer attempts to retrieve tickets from the pool at random intervals.
     * The thread stops when it is interrupted or the system is terminated.
     * </p>
     */
    @Override
    public void run() {
        try {
            // Delay before customers start interacting with the system
            Thread.sleep(3000); // 3-second delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Customer-" + customerId + " interrupted before starting.");
            return;
        }

        while (!Thread.currentThread().isInterrupted() && !ticketPool.isTerminated()) {
            try {
                // Simulate ticket retrieval delay
                Thread.sleep(random.nextInt(2000) + 1000); // 1 to 3 seconds

                Ticket ticket = ticketPool.removeTicket(customerId);
                if (ticket == null) {
                    System.out.println("Customer-" + customerId + " attempted to retrieve a ticket but none were available.");
                } else {
                    System.out.println("Customer-" + customerId + " successfully retrieved " + ticket);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
