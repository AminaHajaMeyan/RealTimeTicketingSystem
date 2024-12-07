package com.amina.backend.model;

import com.amina.backend.service.TicketService;

public class Vendor implements Runnable {
    private final int vendorId; // Unique Vendor ID
    private final TicketService ticketService;
    private final int releaseRate; // Number of tickets to release per cycle

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
                    boolean success = ticketService.addTicket("Vendor-" + vendorId);
                    if (!success) {
                        // Log or handle scenario where the ticket pool is full
                        System.out.println("Vendor-" + vendorId + ": Ticket pool is full, cannot add tickets.");
                    }
                }
                Thread.sleep(2000); // Delay to simulate vendor action
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Ensure thread exit on interruption
            System.out.println("Vendor-" + vendorId + ": Interrupted and exiting.");
        }
    }
}
