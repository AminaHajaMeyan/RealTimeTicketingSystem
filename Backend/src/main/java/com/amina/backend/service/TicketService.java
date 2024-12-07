package com.amina.backend.service;

import com.amina.backend.config.TicketConfig;
import com.amina.backend.model.Customer;
import com.amina.backend.model.TicketPool;
import com.amina.backend.model.Vendor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TicketService {
    private TicketConfig config;
    private TicketPool ticketPool;
    private boolean systemRunning = false;
    private ExecutorService executor;

    public void configureSystem(TicketConfig config) {
        validateConfig(config);
        this.config = config;
        this.ticketPool = new TicketPool(config.getMaxCapacity());
    }

    public void startSystem() {
        if (config == null) {
            throw new IllegalStateException("System is not configured.");
        }
        systemRunning = true;

        executor = Executors.newFixedThreadPool(20); // Fixed threads for 10 vendors + 10 customers
        for (int i = 1; i <= 10; i++) {
            executor.submit(new Vendor(i, this, config.getReleaseRate()));
            executor.submit(new Customer(i, this, config.getRetrievalRate()));
        }
    }

    public void stopSystem() {
        systemRunning = false;
        if (executor != null) {
            executor.shutdownNow();
        }
    }
    public boolean isSystemRunning() {
        return systemRunning;
    }

    public void resetSystem() {
        stopSystem();
        ticketPool.clear();
    }

    public boolean addTicket(String vendorId) {
        return ticketPool.addTicket("Ticket by " + vendorId);
    }

    public boolean purchaseTicket(String customerId) {
        return ticketPool.removeTicket() != null;
    }

    private void validateConfig(TicketConfig config) {
        if (config.getMaxCapacity() > config.getTotalTickets()) {
            throw new IllegalArgumentException("Max capacity cannot exceed total tickets.");
        }
        if (config.getReleaseRate() > config.getMaxCapacity()) {
            throw new IllegalArgumentException("Release rate cannot exceed max ticket capacity.");
        }
        if (config.getRetrievalRate() > config.getMaxCapacity()) {
            throw new IllegalArgumentException("Retrieval rate cannot exceed max ticket capacity.");
        }
    }
}
