package com.amina.backend.service;

import com.amina.backend.config.TicketConfig;
import com.amina.backend.model.Customer;
import com.amina.backend.model.TicketPool;
import com.amina.backend.model.Vendor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TicketService {
    private TicketConfig config;
    private TicketPool ticketPool;
    private boolean systemRunning = false;
    private ExecutorService executor;
    private final AtomicInteger totalTicketsAdded = new AtomicInteger(0);
    private final AtomicInteger totalTicketsSold = new AtomicInteger(0);

    public void configureSystem(TicketConfig config) {
        config.validate();
        this.config = config;
        this.ticketPool = new TicketPool(config.getMaxCapacity());
        saveConfigToFile("system-config.json");
        System.out.println("Configuration Complete: " + getSummary());
    }

    public void startSystem() {
        if (systemRunning) throw new IllegalStateException("System is already running.");
        systemRunning = true;
        executor = Executors.newFixedThreadPool(15);
        startVendors();
        startCustomers();
    }

    public void stopSystem() {
        systemRunning = false;
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("System Stopped: " + getSummary());
    }

    public void resetSystem() {
        stopSystem();
        if (ticketPool != null) ticketPool.clear();
        totalTicketsAdded.set(0);
        totalTicketsSold.set(0);
        System.out.println("System Reset.");
    }

    public boolean isSystemRunning() {
        return systemRunning && totalTicketsSold.get() < config.getTotalTickets();
    }

    public synchronized boolean addTicket(String vendorId) {
        if (totalTicketsAdded.get() >= config.getTotalTickets()) {
            return false; // Stop adding tickets if total limit is reached
        }
        if (ticketPool.getCurrentSize() >= config.getMaxCapacity()) {
            System.out.println(vendorId + " cannot add tickets. Pool is full. Vendors waiting...");
            return false; // Pool is full
        }
        int ticketId = totalTicketsAdded.incrementAndGet();
        String ticket = "Ticket[ID=" + ticketId + ", Seat='Seat-" + ticketId + "', Event='Concert Event']";
        ticketPool.addTicket(ticket);
        System.out.println(vendorId + " added " + ticket + ". Current pool size: " +
                ticketPool.getCurrentSize() + "/" + config.getMaxCapacity());
        return true;
    }

    public synchronized boolean purchaseTicket(String customerId) {
        if (totalTicketsSold.get() >= config.getTotalTickets()) {
            systemRunning = false; // Stop the system when all tickets are sold
            System.out.println("All tickets sold. Stopping the system.");
            return false;
        }
        String ticket = ticketPool.removeTicket();
        if (ticket != null) {
            int ticketId = totalTicketsSold.incrementAndGet();
            System.out.println(customerId + " purchased: Ticket[ID=" + ticketId +
                    ", Seat='Seat-" + ticketId + "', Event='Concert Event']");
            return true;
        }
        return false; // No tickets available
    }

    public String getSummary() {
        return "Summary: " +
                "Total Tickets Sold = " + totalTicketsSold.get() +
                ", Current Pool Size = " + ticketPool.getCurrentSize() +
                ", Total Tickets Configured = " + config.getTotalTickets() +
                ", Max Capacity = " + config.getMaxCapacity() +
                ", System Running = " + systemRunning;
    }

    private void startVendors() {
        for (int i = 1; i <= 5; i++) { // Number of vendors
            int vendorId = i;
            executor.submit(new Vendor(vendorId, this)); // Submit Vendor thread
        }
    }

    private void startCustomers() {
        for (int i = 1; i <= 10; i++) { // Number of customers
            int customerId = i;
            executor.submit(new Customer(customerId, this)); // Submit Customer thread
        }
    }


    private void saveConfigToFile(String filePath) {
        try {
            new ObjectMapper().writeValue(new File(filePath), config);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save configuration to file.", e);
        }
    }
}
