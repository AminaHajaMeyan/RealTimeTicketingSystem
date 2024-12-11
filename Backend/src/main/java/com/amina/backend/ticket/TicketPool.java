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
        if (tickets.size() >= maxTicketCapacity) {
            return false; // Pool is full
        }
        tickets.add(ticket);
        notifyAll(); // Notify waiting customers
        webSocketHandler.broadcastMessage(String.format("Vendor-%d added Ticket %s", vendorId, ticket));
        return true;
    }

    public synchronized Ticket removeTicket(int customerId) {
        Ticket ticket = tickets.poll();
        if (ticket != null) {
            totalTicketsSold++;
            webSocketHandler.broadcastMessage(String.format(
                    "Customer-%d purchased and retrieved Ticket %s. Total Tickets Sold: %d/%d",
                    customerId, ticket, totalTicketsSold, totalTickets));
        }
        return ticket;
    }
    public synchronized void stopSystem() {
        isTerminated = true;
        notifyAll(); // Notify all waiting threads

    }

    public synchronized boolean isTerminated() {
        return totalTicketsSold >= totalTickets;
    }


    public synchronized int getTotalTicketsSold() {
        return totalTicketsSold;
    }

    public synchronized int getTicketsInPool() {
        return tickets.size();
    }

    public synchronized String generateSummary(boolean manuallyStopped) {
        return String.format("Summary: Total Tickets Sold: %d/%d. System was %s.",
                totalTicketsSold, totalTickets, manuallyStopped ? "manually stopped" : "naturally terminated");
    }
}
