package com.amina.backend.user;

import com.amina.backend.ticket.Ticket;
import com.amina.backend.ticket.TicketPool;

import java.util.Random;

public class Customer implements Runnable {
    private final TicketPool ticketPool;
    private final int customerId;
    private final Random random = new Random();

    public Customer(TicketPool ticketPool, int customerId) {
        this.ticketPool = ticketPool;
        this.customerId = customerId;
    }

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
                //System.out.println("Customer-" + customerId + " interrupted.");
                break;
            }
        }

        //System.out.println("Customer-" + customerId + " has stopped.");
    }
}
