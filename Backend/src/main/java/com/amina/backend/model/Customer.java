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
                if (!ticketService.purchaseTicket("Customer-" + customerId)) {
                    Thread.sleep(500); // Wait briefly if no tickets are available
                }
                Thread.sleep((long) (Math.random() * 1000)); // Simulate random intervals
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle interruption
        }
    }
}
