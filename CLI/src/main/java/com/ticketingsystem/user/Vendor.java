package com.ticketingsystem.user;

import com.ticketingsystem.logger.Logger;
import com.ticketingsystem.ticket.Ticket;
import com.ticketingsystem.ticket.TicketPool;

public class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int vendorId;
    private static int ticketCounter = 0; // Shared counter for unique ticket IDs

    public Vendor(TicketPool ticketPool, int vendorId) {
        this.ticketPool = ticketPool;
        this.vendorId = vendorId;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted() && !ticketPool.isTerminated()) {
                Ticket ticket = generateTicket();
                ticketPool.addTicket(ticket, vendorId);
                Thread.sleep(2000L); // Simulate ticket release delay
            }
        } catch (InterruptedException e) {
            // Log once before exiting the thread
            if (!ticketPool.isTerminated()) {
                Logger.log("Vendor " + vendorId + " interrupted.");
            }
        }
    }



    private synchronized Ticket generateTicket() {
        int ticketId = ++ticketCounter; // Generate unique ticket ID
        String seatNumber = "Seat-" + ticketId;
        return new Ticket(ticketId, seatNumber); // Create a new ticket
    }
}

