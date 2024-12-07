package com.amina.backend.model;

import java.util.LinkedList;
import java.util.Queue;

public class TicketPool {
    private final int maxCapacity;
    private final Queue<String> tickets = new LinkedList<>();

    public TicketPool(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public synchronized boolean addTicket(String ticket) {
        if (tickets.size() < maxCapacity) {
            tickets.add(ticket);
            return true;
        }
        return false;
    }

    public synchronized String removeTicket() {
        return tickets.poll();
    }

    public synchronized int getRemainingTickets() {
        return tickets.size();
    }
}
