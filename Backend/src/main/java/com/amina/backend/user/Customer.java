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
            Thread.sleep(3000); // Customers start after a 5-second delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        while (!Thread.currentThread().isInterrupted() && !ticketPool.isTerminated()) {
            try {
                if (ticketPool.isTerminated()) {
                    break;
                }

                // Simulate ticket retrieval delay
                Thread.sleep(random.nextInt(2000) + 1000); // 1 to 3 seconds

                Ticket ticket = ticketPool.removeTicket(customerId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
