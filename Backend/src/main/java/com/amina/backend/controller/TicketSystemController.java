package com.amina.backend.controller;

import com.amina.backend.configuration.Configuration;
import com.amina.backend.configuration.ConfigurationManager;
import com.amina.backend.ticket.TicketPool;
import com.amina.backend.user.Customer;
import com.amina.backend.user.Vendor;
import com.amina.backend.websocket.ActivityWebSocketHandler;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing the ticketing system.
 * <p>
 * This controller provides endpoints for configuring, starting, stopping, and resetting the system.
 * It handles the lifecycle of the system and manages vendor and customer threads.
 * </p>
 *
 * @author Amina
 * @version 1.0
 * @since 2024-12-11
 */
@RestController
@RequestMapping("/ticket-system")
public class TicketSystemController {
    private final ConfigurationManager configManager;
    private final ActivityWebSocketHandler webSocketHandler;
    private TicketPool ticketPool;
    private final List<Thread> vendorThreads = new ArrayList<>();
    private final List<Thread> customerThreads = new ArrayList<>();
    private boolean isRunning = false; // Flag to track system state

    /**
     * Constructs a new {@code TicketSystemController}.
     *
     * @param configManager    The configuration manager.
     * @param webSocketHandler The WebSocket handler for broadcasting updates.
     */
    public TicketSystemController(ConfigurationManager configManager, ActivityWebSocketHandler webSocketHandler) {
        this.configManager = configManager;
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * Configures the system with the provided configuration.
     *
     * @param config The configuration to set.
     * @return A response indicating the result of the operation.
     */
    @PostMapping("/configure")
    public ResponseEntity<String> configureSystem(@RequestBody @Valid Configuration config) {
        if (isRunning) {
            return ResponseEntity.badRequest().body("System is already running. Stop the system before reconfiguring.");
        }

        if (config.getTotalTickets() <= 0 || config.getMaxTicketCapacity() <= 0 ||
                config.getTotalTickets() < config.getMaxTicketCapacity()) {
            return ResponseEntity.badRequest().body("Invalid configuration parameters.");
        }

        configManager.saveConfig(config);
        configManager.printConfigSummary(config);
        this.ticketPool = new TicketPool(config.getMaxTicketCapacity(), config.getTotalTickets(), webSocketHandler);

        return ResponseEntity.ok("System configured successfully.");
    }

    /**
     * Starts the ticketing system.
     *
     * @return A response indicating the result of the operation.
     */
    @PostMapping("/start")
    public ResponseEntity<String> startSystem() {
        if (ticketPool == null) {
            return ResponseEntity.badRequest().body("System not configured. Please configure the system before starting.");
        }

        if (isRunning) {
            return ResponseEntity.badRequest().body("System is already running.");
        }

        isRunning = true;
        startThreads();
        monitorSystemTermination();

        return ResponseEntity.ok("System started successfully.");
    }

    /**
     * Stops the ticketing system manually.
     *
     * @return A response indicating the result of the operation.
     */
    @PostMapping("/stop")
    public ResponseEntity<String> stopSystem() {
        if (!isRunning) {
            return ResponseEntity.badRequest().body("System is not running.");
        }

        ticketPool.stopSystem();
        stopAllThreads();
        String summary = ticketPool.generateSummary(true);
        webSocketHandler.broadcastMessage("[System] Manual stop triggered.\n" + summary);
        isRunning = false;

        return ResponseEntity.ok("System stopped successfully.");
    }

    /**
     * Resets the runtime state of the system while retaining the configuration.
     *
     * @return A response indicating the result of the operation.
     */
    @PostMapping("/reset")
    public String resetSystem() {
        if (ticketPool == null) {
            return "System is not initialized or already reset.";
        }

        stopAllThreads();
        ticketPool = null;
        webSocketHandler.broadcastMessage("[System] Reset triggered. System is ready for reconfiguration.");

        return "System runtime state has been reset. Configuration remains intact.";
    }

    /**
     * Retrieves the current status of the system.
     *
     * @return A response with the system status.
     */
    @GetMapping("/status")
    public ResponseEntity<String> getSystemStatus() {
        if (ticketPool == null) {
            return ResponseEntity.ok("System not configured.");
        }

        return ResponseEntity.ok("System Status: " +
                "\nTickets Sold: " + ticketPool.getTotalTicketsSold() +
                "\nTickets in Pool: " + ticketPool.getTicketsInPool() +
                "\nSystem Terminated: " + ticketPool.isTerminated());
    }

    /**
     * Starts the vendor and customer threads.
     */
    private void startThreads() {
        for (int i = 0; i < 5; i++) {
            Vendor vendor = new Vendor(ticketPool, i + 1);
            Thread vendorThread = new Thread(vendor, "Vendor-" + (i + 1));
            vendorThreads.add(vendorThread);
            vendorThread.start();
        }

        for (int i = 0; i < 10; i++) {
            Customer customer = new Customer(ticketPool, i + 1);
            Thread customerThread = new Thread(customer, "Customer-" + (i + 1));
            customerThreads.add(customerThread);
            customerThread.start();
        }
    }

    /**
     * Monitors the termination of the system and stops threads when necessary.
     */
    private void monitorSystemTermination() {
        new Thread(() -> {
            try {
                while (!ticketPool.isTerminated()) {
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            stopAllThreads();

            String summary = ticketPool.generateSummary(false);
            System.out.println(summary);
            webSocketHandler.broadcastMessage(summary);

            isRunning = false;
        }).start();
    }

    /**
     * Stops all vendor and customer threads.
     */
    private void stopAllThreads() {
        vendorThreads.forEach(thread -> {
            if (thread.isAlive()) {
                thread.interrupt();
            }
        });

        customerThreads.forEach(thread -> {
            if (thread.isAlive()) {
                thread.interrupt();
            }
        });

        vendorThreads.clear();
        customerThreads.clear();
    }
}
