package com.amina.backend.service;

import com.amina.backend.config.TicketConfig;
import com.amina.backend.model.TicketPool;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Service
public class TicketService {
    private TicketConfig config;
    private TicketPool ticketPool;
    private boolean systemRunning = false;
    private ExecutorService executor;
    private final AtomicInteger totalTicketsAdded = new AtomicInteger(0);
    private final AtomicInteger totalTicketsSold = new AtomicInteger(0);
    private static final Logger logger = Logger.getLogger(TicketService.class.getName());

    public void configureSystem(TicketConfig config) {
        validateConfig(config);
        this.config = config;
        this.ticketPool = new TicketPool(config.getMaxCapacity());
        logger.info("System configured: Total Tickets = " + config.getTotalTickets() +
                ", Max Capacity = " + config.getMaxCapacity() +
                ", Release Rate = " + config.getReleaseRate() +
                ", Retrieval Rate = " + config.getRetrievalRate());
    }

    public void startSystem() {
        if (config == null) {
            throw new IllegalStateException("System is not configured.");
        }
        if (systemRunning) {
            logger.warning("System is already running.");
            return;
        }

        ticketPool.clear(); // Ensure the pool starts empty
        systemRunning = true;
        executor = Executors.newFixedThreadPool(15); // 10 customers + 5 vendors
        logger.info("System started. Vendors and customers are now active.");

        // Start vendors
        for (int i = 1; i <= 5; i++) {
            final int vendorId = i; // Final copy for lambda
            executor.submit(() -> {
                try {
                    while (isSystemRunning()) {
                        Thread.sleep(config.getReleaseRate() * 100); // Delay based on release rate
                        addTicket("Vendor-" + vendorId);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.info("Vendor-" + vendorId + " interrupted. Exiting.");
                }
            });
        }

        // Start customers
        for (int i = 1; i <= 10; i++) {
            final int customerId = i; // Final copy for lambda
            executor.submit(() -> {
                try {
                    while (isSystemRunning()) {
                        Thread.sleep(config.getRetrievalRate() * 100); // Delay based on retrieval rate
                        purchaseTicket("Customer-" + customerId);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.info("Customer-" + customerId + " interrupted. Exiting.");
                }
            });
        }
    }

    public void stopSystem() {
        systemRunning = false;
        if (executor != null) {
            executor.shutdownNow();
        }
        logger.info("System stopped.");
    }

    public void resetSystem() {
        stopSystem();
        if (ticketPool != null) {
            ticketPool.clear();
        }
        totalTicketsAdded.set(0);
        totalTicketsSold.set(0);
        logger.info("System reset.");
    }

    public synchronized boolean addTicket(String vendorId) {
        if (totalTicketsAdded.get() >= config.getTotalTickets()) {
            logger.info("Maximum number of tickets reached. Vendor " + vendorId + " cannot add more tickets.");
            return false;
        }
        int ticketId = totalTicketsAdded.incrementAndGet();
        String ticket = "Ticket[ID=" + ticketId + ", Seat='Seat-" + ticketId + "', Event='Concert Event']";
        boolean success = ticketPool.addTicket(ticket);
        if (success) {
            logger.info("Vendor " + vendorId + " added " + ticket + ". Current pool size: " +
                    ticketPool.getCurrentSize() + "/" + config.getMaxCapacity());
        }
        return success;
    }

    public synchronized boolean purchaseTicket(String customerId) {
        String ticket = ticketPool.removeTicket();
        if (ticket != null) {
            logger.info("Customer " + customerId + " purchased: " + ticket +
                    ". Total tickets sold: " + totalTicketsSold.incrementAndGet());
            return true;
        } else {
            logger.warning("Customer " + customerId + " could not purchase a ticket. Pool is empty.");
            return false;
        }
    }

    public boolean isSystemRunning() {
        return systemRunning && totalTicketsSold.get() < config.getTotalTickets();
    }

    private void validateConfig(TicketConfig config) {
        if (config.getTotalTickets() <= 0) {
            throw new IllegalArgumentException("Total tickets must be greater than 0.");
        }
        if (config.getMaxCapacity() <= 0 || config.getMaxCapacity() > config.getTotalTickets()) {
            throw new IllegalArgumentException("Max capacity must be less than or equal to total tickets.");
        }
        if (config.getReleaseRate() <= 0 || config.getReleaseRate() > config.getMaxCapacity()) {
            throw new IllegalArgumentException("Release rate must be positive and less than or equal to max capacity.");
        }
        if (config.getRetrievalRate() <= 0 || config.getRetrievalRate() > config.getMaxCapacity()) {
            throw new IllegalArgumentException("Retrieval rate must be positive and less than or equal to max capacity.");
        }
    }
}
