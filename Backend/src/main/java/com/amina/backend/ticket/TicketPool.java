package com.amina.backend.ticket;

import com.amina.backend.websocket.ActivityWebSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class TicketPool {
    private final List<Ticket> tickets = new LinkedList<>();
    private final int maxTicketCapacity;
    private final int totalTickets;
    private int totalTicketsSold = 0;
    private int ticketsAddedToPool = 0;
    private boolean isTerminated = false;

    private final List<String> waitingVendors = new ArrayList<>();
    private final List<String> waitingCustomers = new ArrayList<>();

    private final ActivityWebSocketHandler webSocketHandler;

    public TicketPool(
            @Value("${max.ticket.capacity:50}") int maxTicketCapacity,
            @Value("${total.tickets:200}") int totalTickets,
            ActivityWebSocketHandler webSocketHandler
    ) {
        this.maxTicketCapacity = maxTicketCapacity;
        this.totalTickets = totalTickets;
        this.webSocketHandler = webSocketHandler;

        if (maxTicketCapacity <= 0 || totalTickets <= 0) {
            throw new IllegalArgumentException("Capacity and total tickets must be greater than zero.");
        }

        System.out.println("Initialized TicketPool with maxTicketCapacity=" + maxTicketCapacity +
                ", totalTickets=" + totalTickets);
    }

    public synchronized void addTicket(Ticket ticket, int vendorId) {
        if (isTerminated || ticketsAddedToPool >= totalTickets) {
            return; // Stop adding tickets if totalTickets is reached
        }

        String vendorName = "Vendor-" + vendorId;

        while (tickets.size() >= maxTicketCapacity) {
            try {
                if (!waitingVendors.contains(vendorName)) {
                    waitingVendors.add(vendorName);
                    System.out.printf("Vendors waiting: %s%n", String.join(", ", waitingVendors));
                }
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } finally {
                waitingVendors.remove(vendorName);
            }
        }

        tickets.add(ticket);
        ticketsAddedToPool++;
        String message = String.format("[Vendor-%d] Added Ticket[ID=%d, Seat='%s', Event='%s']. Current pool size: %d/%d",
                vendorId, ticket.getTicketId(), ticket.getSeatNumber(), ticket.getEventName(), tickets.size(), maxTicketCapacity);
        System.out.println(message);

        // Broadcast via WebSocket
        webSocketHandler.broadcastMessage(message);

        notifyAll();
    }

    public synchronized Ticket removeTicket(int customerId) {
        if (isTerminated || totalTicketsSold >= totalTickets) {
            return null; // Stop retrieving tickets if all tickets are sold
        }

        while (tickets.isEmpty()) {
            if (isTerminated || totalTicketsSold >= totalTickets) {
                return null;
            }

            // Log each customer waiting individually
            System.out.printf("[Customer-%d] is waiting for a ticket.%n", customerId);

            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        Ticket ticket = tickets.remove(0);
        totalTicketsSold++;
        String message = String.format("[Customer-%d] Purchased: Ticket[ID=%d, Seat='%s', Event='%s']. Total Tickets Sold: %d/%d",
                customerId, ticket.getTicketId(), ticket.getSeatNumber(), ticket.getEventName(), totalTicketsSold, totalTickets);
        System.out.println(message);

        // Broadcast via WebSocket
        webSocketHandler.broadcastMessage(message);

        if (totalTicketsSold >= totalTickets) {
            System.out.println("\n[System] All tickets sold! Terminating system...\n");
            isTerminated = true;

            // Notify WebSocket clients
            webSocketHandler.broadcastMessage("[System] All tickets sold! Terminating system.");
        }

        notifyAll();
        return ticket;
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
        String statusMessage = manuallyStopped
                ? "[System] Stopped manually before completing ticket sales."
                : "[System] All tickets sold!";
        int remainingTickets = totalTickets - ticketsAddedToPool;

        String summary = String.format("\n=== System Summary ===\n" +
                        "%s\n" +
                        "Total Tickets Sold: %d\n" +
                        "Total Customers Served: %d\n" +
                        "Total Tickets Remaining in Pool: %d\n" +
                        "Remaining Tickets (not added to pool): %d\n" +
                        "=========================\n",
                statusMessage, totalTicketsSold, totalTicketsSold, tickets.size(), remainingTickets);

        // Broadcast summary via WebSocket
        webSocketHandler.broadcastMessage(summary);

        return summary;
    }

    public synchronized void stopSystem() {
        if (!isTerminated) {
            isTerminated = true;
            notifyAll(); // Wake up waiting threads
            System.out.println("[System] Manual stop triggered.");

            // Notify WebSocket clients
            webSocketHandler.broadcastMessage("[System] Manual stop triggered.");
        }
    }
}
