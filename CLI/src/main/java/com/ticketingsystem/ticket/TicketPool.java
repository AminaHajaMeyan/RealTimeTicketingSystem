package com.ticketingsystem.ticket;

import com.ticketingsystem.logger.Logger;

import java.util.LinkedList;
import java.util.List;

public class TicketPool {
    private final List<Ticket> tickets = new LinkedList<>();
    private final int maxTicketCapacity;
    private final int totalTickets;
    private int totalTicketsSold = 0;
    private boolean isTerminated = false;

    private int waitingCustomers = 0;
    private int waitingVendors = 0;
    private long lastLoggedTime = System.currentTimeMillis();

    public TicketPool(int maxTicketCapacity, int totalTickets) {
        this.maxTicketCapacity = maxTicketCapacity;
        this.totalTickets = totalTickets;
    }

    public synchronized void addTicket(Ticket ticket, int vendorId) {
        if (isTerminated || totalTicketsSold >= totalTickets) return;

        while (tickets.size() >= maxTicketCapacity) {
            try {
                waitingVendors++;
                logWaitingSummary(); // Log vendor and customer waiting summary
                Logger.log("Vendor " + vendorId + " waiting to add tickets...");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Logger.log("Vendor " + vendorId + " interrupted.");
                return;
            } finally {
                waitingVendors--; // Decrement waiting vendor count when no longer waiting
            }
        }

        tickets.add(ticket);
        Logger.log("Vendor " + vendorId + " added Ticket[ID=" + ticket.getTicketId() +
                ", Seat='" + ticket.getSeatNumber() + "', Event='" + ticket.getEventName() +
                "']. Current pool size: " + tickets.size() + "/" + maxTicketCapacity);
        notifyAll(); // Notify waiting customers
    }

    public synchronized Ticket removeTicket() {
        if (isTerminated || totalTicketsSold >= totalTickets) return null;

        while (tickets.isEmpty()) {
            if (isTerminated || totalTicketsSold >= totalTickets) return null;

            try {
                waitingCustomers++;
                logWaitingSummary(); // Log vendor and customer waiting summary
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Logger.log("Customer interrupted.");
                return null;
            } finally {
                waitingCustomers--; // Decrement waiting customer count when no longer waiting
            }
        }

        Ticket ticket = tickets.remove(0);
        totalTicketsSold++;
        Logger.log("Ticket purchased: " + ticket + ". Total tickets sold: " + totalTicketsSold);

        if (totalTicketsSold >= totalTickets) {
            Logger.log("All tickets sold! Terminating the system...");
            isTerminated = true;
            getRealTimeStatus();
        }

        notifyAll(); // Notify waiting vendors
        return ticket;
    }

    private void logWaitingSummary() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastLoggedTime >= 5000) { // Log every 5 seconds
            if (waitingCustomers > 0 || waitingVendors > 0) {
                Logger.log(waitingCustomers + " customers and " + waitingVendors + " vendors are currently waiting.");
            }
            lastLoggedTime = currentTime;
        }
    }

    public synchronized int getCurrentPoolSize() {
        return tickets.size();
    }

    public synchronized int getTotalTicketsSold() {
        return totalTicketsSold;
    }

    public synchronized int getRemainingTickets() {
        return totalTickets - totalTicketsSold;
    }

    public synchronized boolean isTerminated() {
        return isTerminated;
    }

    public synchronized String getRealTimeStatus() {
        return "Real-Time Status: Pool Size: " + tickets.size() +
                ", Tickets Sold: " + totalTicketsSold +
                ", Remaining Tickets: " + getRemainingTickets() +
                ", Waiting Customers: " + waitingCustomers +
                ", Waiting Vendors: " + waitingVendors;
    }
}

