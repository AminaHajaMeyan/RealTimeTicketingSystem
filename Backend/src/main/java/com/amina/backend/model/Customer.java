package com.amina.backend.model;

import com.amina.backend.service.TicketService;

public class Customer implements Runnable {
    private final int customerId; // Unique Customer ID
    private final TicketService ticketService;
    private final int retrievalRate; // Number of tickets to retrieve per cycle

    public Customer(int customerId, TicketService ticketService, int retrievalRate) {
        this.customerId = customerId;
        this.ticketService = ticketService;
        this.retrievalRate = retrievalRate;
    }

    @Override
    public void run() {
        try {
            while (ticketService.isSystemRunning()) {
                for (int i = 0; i < retrievalRate; i++) {
                    boolean success = ticketService.purchaseTicket("Customer-" + customerId);
                    if (!success) {
                        // Log or handle scenario where no tickets are available
                        System.out.println("Customer-" + customerId + ": No tickets available to purchase.");
                    }
                }
                Thread.sleep(1000); // Delay to simulate customer action
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Ensure thread exit on interruption
            System.out.println("Customer-" + customerId + ": Interrupted and exiting.");
        }
    }
}
