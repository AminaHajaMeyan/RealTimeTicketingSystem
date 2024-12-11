package com.amina.backend.ticket;

import com.amina.backend.websocket.ActivityWebSocketHandler;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TicketPool {
    private final ConcurrentLinkedQueue<Ticket> tickets = new ConcurrentLinkedQueue<>();
    private final int maxTicketCapacity;
    private final int totalTickets;
    private int totalTicketsSold = 0;
    private boolean isTerminated = false;
    private final ActivityWebSocketHandler webSocketHandler;

    public TicketPool(int maxTicketCapacity, int totalTickets, ActivityWebSocketHandler webSocketHandler) {
        this.maxTicketCapacity = maxTicketCapacity;
        this.totalTickets = totalTickets;
        this.webSocketHandler = webSocketHandler;
    }

    public synchronized boolean addTicket(Ticket ticket, int vendorId) {
        if (totalTicketsSold + tickets.size() >= totalTickets) {
            System.out.println("Vendor-" + vendorId + ": Cannot add more tickets. Total tickets limit reached.");
            return false;
        }

        if (tickets.size() >= maxTicketCapacity) {
            System.out.println("Vendor-" + vendorId + ": Pool is full. Waiting to add more tickets.");
            return false;
        }

        tickets.add(ticket);
        notifyAll(); // Notify waiting customers
        webSocketHandler.broadcastMessage(String.format("Vendor-%d added %s", vendorId, ticket));
        return true;
    }


    public synchronized Ticket removeTicket(int customerId) {
        if (isTerminated()) {
            return null; // No more tickets should be sold after termination
        }

        Ticket ticket = tickets.poll();
        if (ticket != null) {
            totalTicketsSold++;
            if (totalTicketsSold > totalTickets) {
                totalTicketsSold--; // Revert the increment if it exceeds the limit
                System.out.println("Error: Total tickets sold exceeds the configured totalTickets.");
                return null;
            }
            webSocketHandler.broadcastMessage(String.format(
                    "Customer-%d successfully retrieved %s. Total Tickets Sold: %d/%d. Remaining Tickets: %d",
                    customerId, ticket, totalTicketsSold, totalTickets, getRemainingTickets()));
        }
        return ticket;
    }

    public synchronized void stopSystem() {
        isTerminated = true;
        notifyAll(); // Notify all waiting threads

    }

    public synchronized boolean isTerminated() {
        if (totalTicketsSold >= totalTickets && tickets.isEmpty()) {
            isTerminated = true;
        }
        return isTerminated;
    }


    public synchronized int getRemainingTickets() {
        return totalTickets - totalTicketsSold;
    }

    public synchronized int getTotalTicketsSold() {
        return totalTicketsSold;
    }

    public synchronized int getTicketsInPool() {
        return tickets.size();
    }

    public synchronized String generateSummary(boolean manuallyStopped) {
        return String.format("Summary: Total Tickets Sold: %d/%d, Tickets Remaining: %d. System was %s.",
                totalTicketsSold, totalTickets, getRemainingTickets(),
                manuallyStopped ? "manually stopped" : "naturally terminated");
    }
}
