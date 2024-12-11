package com.amina.backend.ticket;

import com.amina.backend.websocket.ActivityWebSocketHandler;

import java.util.LinkedList;
import java.util.List;

public class TicketPool {
    private final List<Ticket> tickets = new LinkedList<>();
    private final int maxTicketCapacity;
    private final int totalTickets;
    private int totalTicketsSold = 0;
    private boolean isTerminated = false;

    private final ActivityWebSocketHandler webSocketHandler;

    public TicketPool(int maxTicketCapacity, int totalTickets, ActivityWebSocketHandler webSocketHandler) {
        if (maxTicketCapacity <= 0 || totalTickets <= 0) {
            throw new IllegalArgumentException("Capacity and total tickets must be greater than zero.");
        }
        this.maxTicketCapacity = maxTicketCapacity;
        this.totalTickets = totalTickets;
        this.webSocketHandler = webSocketHandler;

        System.out.println("=== Configuration ===");
        System.out.println("Initialized TicketPool with maxTicketCapacity=" + maxTicketCapacity + ", totalTickets=" + totalTickets);
    }

    public synchronized void addTicket(Ticket ticket, int vendorId) {
        if (totalTicketsSold >= totalTickets || isTerminated) {
            System.out.println("Vendor-" + vendorId + " attempted to add a ticket, but the limit has been reached.");
            return;
        }

        while (tickets.size() >= maxTicketCapacity) {
            try {
                wait(); // Vendors wait if the pool is full
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        tickets.add(ticket);
        System.out.println("Vendor-" + vendorId + " added Ticket{ticketId=" + ticket.getTicketId() + ", eventName='" + ticket.getEventName() + "', seatNumber=" + ticket.getSeatNumber() + "}.");

        // Broadcast vendor activity
        webSocketHandler.broadcastMessage(String.format(
                "{\"type\": \"TICKET_ADDED\", \"vendorId\": %d, \"ticket\": {\"ticketId\": %d, \"eventName\": \"%s\", \"seatNumber\": %d}, \"ticketsInPool\": %d}",
                vendorId, ticket.getTicketId(), ticket.getEventName(), ticket.getSeatNumber(), tickets.size()
        ));

        notifyAll(); // Notify all waiting customers
    }

    public synchronized Ticket removeTicket(int customerId) {
        while (tickets.isEmpty()) {
            if (isTerminated || totalTicketsSold >= totalTickets) {
                System.out.println("Customer-" + customerId + " could not retrieve a ticket. Total Tickets Sold: " + totalTicketsSold + "/" + totalTickets + ".");
                return null;
            }
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        Ticket ticket = tickets.remove(0);
        totalTicketsSold++;

        if (totalTicketsSold >= totalTickets) {
            isTerminated = true;
            System.out.println("=== System Termination ===");
            System.out.println("TicketPool: All tickets sold. System is now terminated.");
        }

        System.out.println("Customer-" + customerId + " purchased and retrieved Ticket{ticketId=" + ticket.getTicketId() + ", eventName='" + ticket.getEventName() + "', seatNumber=" + ticket.getSeatNumber() + "}. Total Tickets Sold: " + totalTicketsSold + "/" + totalTickets + ".");

        // Broadcast customer activity
        webSocketHandler.broadcastMessage(String.format(
                "{\"type\": \"TICKET_SOLD\", \"customerId\": %d, \"ticket\": {\"ticketId\": %d, \"eventName\": \"%s\", \"seatNumber\": %d}, \"totalTicketsSold\": %d}",
                customerId, ticket.getTicketId(), ticket.getEventName(), ticket.getSeatNumber(), totalTicketsSold
        ));

        notifyAll(); // Notify vendors to add more tickets
        return ticket;
    }

    public synchronized void stopSystem() {
        isTerminated = true;
        notifyAll();
        System.out.println("System has been marked as terminated.");
    }

    public synchronized boolean isTerminated() {
        return isTerminated;
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
