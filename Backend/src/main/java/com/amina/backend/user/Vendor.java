package com.amina.backend.user;

import com.amina.backend.ticket.Ticket;
import com.amina.backend.ticket.TicketPool;

import java.util.Random;

/**
 * Represents a vendor in the ticketing system.
 * <p>
 * A {@code Vendor} is responsible for adding tickets to the shared {@code TicketPool}.
 * Each vendor runs as a separate thread, simulating concurrent ticket producers.
 * </p>
 *
 * @author Amina
 * @version 1.0
 * @since 2024-12-11
 */
public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int vendorId;
    private final Random random = new Random();
    private static int ticketCounter = 1; // Shared counter for ticket IDs

    /**
     * Constructs a new {@code Vendor} with the specified {@code TicketPool} and ID.
     *
     * @param ticketPool The shared ticket pool.
     * @param vendorId   The unique ID of the vendor.
     */
    public Vendor(TicketPool ticketPool, int vendorId) {
        this.ticketPool = ticketPool;
        this.vendorId = vendorId;
    }

    /**
     * The main logic for the vendor's thread.
     * <p>
     * The vendor continuously attempts to add tickets to the pool until the system is terminated
     * or the thread is interrupted.
     * </p>
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && !ticketPool.isTerminated()) {
            try {
                // Simulate ticket creation delay
                Thread.sleep(random.nextInt(2000) + 1000); // 1 to 3 seconds

                Ticket ticket;
                synchronized (Vendor.class) { // Ensure unique ticket IDs
                    ticket = new Ticket(ticketCounter++, "Music Show", ticketCounter);
                }

                boolean added = ticketPool.addTicket(ticket, vendorId);
                if (!added) {
                    System.out.println("Vendor-" + vendorId + ": Pool is full. Waiting to add more tickets.");
                    synchronized (ticketPool) {
                        ticketPool.wait(1000); // Wait before retrying
                    }
                } else {
                    System.out.println("Vendor-" + vendorId + " added Ticket " + ticket);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // Break the loop cleanly on interruption
                break;
            }
        }
    }
}
