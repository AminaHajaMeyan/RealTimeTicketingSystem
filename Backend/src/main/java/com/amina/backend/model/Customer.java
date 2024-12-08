package com.amina.backend.model;

import com.amina.backend.service.TicketService;

public class Customer implements Runnable {
    private final int customerId;
    private final TicketService ticketService;
    private final int retrievalRate;

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
                    ticketService.purchaseTicket("Customer-" + customerId);
                }
                Thread.sleep(1000); // Simulate delay for customers
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
