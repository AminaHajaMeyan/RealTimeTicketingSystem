package com.amina.backend.user;

import com.amina.backend.ticket.Ticket;
import com.amina.backend.ticket.TicketPool;

import java.util.Random;

public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int vendorId;
    private final Random random = new Random();
    private static int ticketCounter = 1; // Shared counter for ticket IDs

    public Vendor(TicketPool ticketPool, int vendorId) {
        this.ticketPool = ticketPool;
        this.vendorId = vendorId;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && !ticketPool.isTerminated()) {
            try {
                if (ticketPool.isTerminated()) {
                    break;
                }

                // Simulate ticket creation delay
                Thread.sleep(random.nextInt(2000) + 1000); // 1 to 3 seconds

                Ticket ticket;
                synchronized (Vendor.class) { // Ensure unique ticket IDs
                    ticket = new Ticket(ticketCounter++, "Music Show", ticketCounter);
                }
                ticketPool.addTicket(ticket, vendorId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
