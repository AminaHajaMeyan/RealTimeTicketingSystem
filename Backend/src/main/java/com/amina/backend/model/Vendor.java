package com.amina.backend.model;

import com.amina.backend.service.TicketService;

public class Vendor implements Runnable {
    private final int vendorId; // Add vendor ID
    private final TicketService ticketService;
    private final int releaseRate;

    public Vendor(int vendorId, TicketService ticketService, int releaseRate) {
        this.vendorId = vendorId;
        this.ticketService = ticketService;
        this.releaseRate = releaseRate;
    }

    @Override
    public void run() {
        try {
            while (ticketService.isSystemRunning()) {
                for (int i = 0; i < releaseRate; i++) {
                    ticketService.addTicket("Vendor-" + vendorId);
                }
                Thread.sleep(2000); // Simulate ticket release delay
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

