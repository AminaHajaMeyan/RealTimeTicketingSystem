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
            return true;
        }
        return false;
    }

    public synchronized String removeTicket() {
        return tickets.poll();
    }

    public synchronized void clear() {
        tickets.clear();
    }

    public synchronized int getCurrentSize() {
        return tickets.size();
    }
}
