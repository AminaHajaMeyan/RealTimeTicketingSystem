package com.amina.backend.user;

import com.amina.backend.ticket.TicketPool;

public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int customerId;

    public Customer(TicketPool ticketPool, int customerId) {
        this.ticketPool = ticketPool;
        this.customerId = customerId;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted() && !ticketPool.isTerminated()) {
                ticketPool.removeTicket(customerId);
                Thread.sleep(1000); // Simulate customer activity
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

