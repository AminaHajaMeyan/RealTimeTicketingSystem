package com.amina.backend.user;

import com.amina.backend.ticket.Ticket;
import com.amina.backend.ticket.TicketPool;

public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int vendorId;

    public Vendor(TicketPool ticketPool, int vendorId) {
        this.ticketPool = ticketPool;
        this.vendorId = vendorId;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted() && !ticketPool.isTerminated()) {
                Ticket ticket = new Ticket(vendorId, "Seat-" + (int) (Math.random() * 100));
                ticketPool.addTicket(ticket, vendorId);
                Thread.sleep(2000); // Simulate vendor activity
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
