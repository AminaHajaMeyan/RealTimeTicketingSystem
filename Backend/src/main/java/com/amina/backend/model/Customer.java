package com.amina.backend.model;

import com.amina.backend.service.TicketService;
public class Customer implements Runnable {
    private final int customerId;
    private final TicketService ticketService;

    public Customer(int customerId, TicketService ticketService) {
        this.customerId = customerId;
        this.ticketService = ticketService;
    }

    @Override
    public void run() {
        try {
            while (ticketService.isSystemRunning()) {
                // Randomly attempt to purchase a ticket
                if (!ticketService.purchaseTicket("Customer-" + customerId)) {
                    Thread.sleep(500); // Wait if pool is empty
                }
                // Random delay between ticket purchases
                Thread.sleep((long) (Math.random() * 1000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
