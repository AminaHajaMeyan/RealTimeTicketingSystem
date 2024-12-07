package com.ticketingsystem.user;

import com.ticketingsystem.logger.Logger;
import com.ticketingsystem.ticket.Ticket;
import com.ticketingsystem.ticket.TicketPool;

public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int customerId; // Unique ID for the customer
    private final int customerRetrievalRate;

    public Customer(TicketPool ticketPool, int customerId, int customerRetrievalRate) {
        this.ticketPool = ticketPool;
        this.customerId = customerId;
        this.customerRetrievalRate = customerRetrievalRate;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted() && !ticketPool.isTerminated()) {
                for (int i = 0; i < customerRetrievalRate; i++) {
                    Ticket ticket = ticketPool.removeTicket();
                    if (ticket != null) {
                        System.out.println("Customer " + customerId + " purchased: " + ticket);
                    }
                }
                Thread.sleep(1000L); // Simulate delay
            }
        } catch (InterruptedException e) {
            // Log once before exiting the thread
            if (!ticketPool.isTerminated()) {
                Logger.log("Customer " + customerId + " interrupted.");
            }
        }
    }

}

