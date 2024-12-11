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
                //System.out.println("Vendor-" + vendorId + " interrupted.");
                break;
            }
        }

        //System.out.println("Vendor-" + vendorId + " has stopped.");
    }
}
