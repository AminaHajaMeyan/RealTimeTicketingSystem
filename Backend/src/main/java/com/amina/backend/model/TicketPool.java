package com.amina.backend.model;

import java.util.LinkedList;
import java.util.Queue;

public class TicketPool {
    private final Queue<String> tickets = new LinkedList<>();
    private final int maxCapacity;

    public TicketPool(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Max capacity must be greater than 0.");
        }
        this.maxCapacity = maxCapacity;
    }

    public synchronized boolean addTicket(String ticket) {
        if (tickets.size() < maxCapacity) {
            tickets.add(ticket);
            System.out.println("Ticket added: " + ticket + " | Current pool size: " + tickets.size() + "/" + maxCapacity);
            return true;
        }
        return false; // Pool is full
    }


    public synchronized String removeTicket() {
        if (tickets.isEmpty()) {
            System.out.println("Cannot remove ticket. Pool is empty.");
            return null;
        }
        String ticket = tickets.poll();
        System.out.println("Ticket removed: " + ticket + " | Remaining pool size: " + tickets.size() + "/" + maxCapacity);
        return ticket;
    }

    public synchronized void clear() {
        tickets.clear();
        System.out.println("All tickets cleared from the pool.");
    }

    public synchronized int getCurrentSize() {
        return tickets.size();
    }
}
