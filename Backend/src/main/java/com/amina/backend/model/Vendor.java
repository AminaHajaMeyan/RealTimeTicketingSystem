package com.amina.backend.model;

import com.amina.backend.service.TicketService;

public class Vendor implements Runnable {
    private final int vendorId;
    private final TicketService ticketService;

    public Vendor(int vendorId, TicketService ticketService) {
        this.vendorId = vendorId;
        this.ticketService = ticketService;
    }

    @Override
    public void run() {
        try {
            while (ticketService.isSystemRunning()) {
                if (!ticketService.addTicket("Vendor-" + vendorId)) {
                    Thread.sleep(500); // Wait briefly if the pool is full
                }
                Thread.sleep((long) (Math.random() * 1000)); // Simulate random intervals
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle interruption
        }
    }
}
