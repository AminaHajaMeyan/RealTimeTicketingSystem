package com.amina.backend.service;

import com.amina.backend.config.TicketActivity;
import com.amina.backend.config.TicketConfig;
import com.amina.backend.model.Customer;
import com.amina.backend.model.TicketPool;
import com.amina.backend.model.Vendor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class TicketService {
    private TicketConfig config;
    private final BlockingQueue<TicketActivity> activities = new LinkedBlockingQueue<>(1000);
    private TicketPool ticketPool;
    private boolean systemRunning = false;
    private ExecutorService executor;

    private static final int TOTAL_VENDORS = 10;
    private static final int TOTAL_CUSTOMERS = 10;

    public void configureSystem(TicketConfig config) {
        validateConfig(config);
        this.config = config;
        this.ticketPool = new TicketPool(config.getMaxCapacity());
        logActivity("System configured with: Total Tickets = " + config.getTotalTickets() +
                ", Max Capacity = " + config.getMaxCapacity() +
                ", Release Rate = " + config.getReleaseRate() +
                ", Retrieval Rate = " + config.getRetrievalRate());
    }

    public void startSystem() {
        if (config == null) {
            logActivity("Error: System not configured.");
            return;
        }
        systemRunning = true;
        logActivity("System started.");

        executor = Executors.newFixedThreadPool(TOTAL_VENDORS + TOTAL_CUSTOMERS);
        for (int i = 1; i <= TOTAL_VENDORS; i++) {
            executor.submit(new Vendor(i, this, config.getReleaseRate()));
        }
        for (int i = 1; i <= TOTAL_CUSTOMERS; i++) {
            executor.submit(new Customer(i, this, config.getRetrievalRate()));
        }
    }

    public void stopSystem() {
        systemRunning = false;
        logActivity("System stopped.");
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public void resetSystem() {
        if (executor != null) {
            executor.shutdownNow();
        }
        activities.clear();
        systemRunning = false;
        logActivity("System reset.");
    }

    public boolean addTicket(String vendorId) {
        if (!systemRunning) {
            logActivity("Vendor operation failed: System is not running.");
            return false;
        }

        boolean added = ticketPool.addTicket("Ticket by " + vendorId);
        if (added) {
            logActivity(vendorId + " added a ticket.");
        } else {
            logActivity(vendorId + " failed to add a ticket. Pool is full.");
        }
        return added;
    }

    public boolean purchaseTicket(String customerId) {
        if (!systemRunning) {
            logActivity("Customer operation failed: System is not running.");
            return false;
        }

        String ticket = ticketPool.removeTicket();
        if (ticket != null) {
            logActivity(customerId + " purchased a ticket.");
            return true;
        } else {
            logActivity(customerId + " failed to purchase a ticket. None available.");
            return false;
        }
    }

    public List<TicketActivity> getActivities() {
        return new ArrayList<>(activities); // Return a copy to prevent modification
    }

    public boolean isSystemRunning() {
        return systemRunning;
    }

    private void logActivity(String message) {
        if (!activities.offer(new TicketActivity(message))) {
            activities.poll(); // Remove oldest entry if full
            activities.offer(new TicketActivity(message));
        }
    }

    private void validateConfig(TicketConfig config) {
        if (config.getTotalTickets() <= 0) {
            throw new IllegalArgumentException("Total tickets must be greater than 0.");
        }
        if (config.getMaxCapacity() <= 0) {
            throw new IllegalArgumentException("Max capacity must be greater than 0.");
        }
        if (config.getReleaseRate() <= 0) {
            throw new IllegalArgumentException("Release rate must be greater than 0.");
        }
        if (config.getRetrievalRate() <= 0) {
            throw new IllegalArgumentException("Retrieval rate must be greater than 0.");
        }
        if (config.getMaxCapacity() > config.getTotalTickets()) {
            throw new IllegalArgumentException("Max capacity cannot exceed total tickets.");
        }
    }
}
