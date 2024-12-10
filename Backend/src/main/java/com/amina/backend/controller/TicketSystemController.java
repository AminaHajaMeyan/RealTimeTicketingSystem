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

@RestController
@RequestMapping("/ticket-system")
public class TicketSystemController {
    private final ConfigurationManager configManager;
    private final ActivityWebSocketHandler webSocketHandler;
    private TicketPool ticketPool;
    private final List<Thread> vendorThreads = new ArrayList<>();
    private final List<Thread> customerThreads = new ArrayList<>();

    public TicketSystemController(ConfigurationManager configManager, ActivityWebSocketHandler webSocketHandler) {
        this.configManager = configManager;
        this.webSocketHandler = webSocketHandler;
    }

    @PostMapping("/configure")
    public ResponseEntity<String> configureSystem(@RequestBody @Valid Configuration config) {
        if (config.getTotalTickets() < config.getMaxTicketCapacity()) {
            return ResponseEntity.badRequest().body("Total tickets must be greater than or equal to max ticket capacity.");
        }

        if (config.getTicketReleaseRate() > config.getMaxTicketCapacity()) {
            return ResponseEntity.badRequest().body("Ticket release rate must not exceed the maximum ticket capacity.");
        }

        if (config.getCustomerRetrievalRate() > config.getMaxTicketCapacity()) {
            return ResponseEntity.badRequest().body("Customer retrieval rate must not exceed the maximum ticket capacity.");
        }

        configManager.saveConfig(config);
        configManager.printConfigSummary(config);

        // Pass the WebSocket handler to TicketPool
        this.ticketPool = new TicketPool(config.getMaxTicketCapacity(), config.getTotalTickets(), webSocketHandler);

        return ResponseEntity.ok("System configured and saved.");
    }

    @PostMapping("/start")
    public String startSystem() {
        int vendors = 5;
        int customers = 10;

        if (ticketPool == null) {
            return "System not configured. Please configure the system before starting.";
        }

        System.out.println("Starting system with " + vendors + " vendors and " + customers + " customers.");

        // Start vendor threads
        for (int i = 0; i < vendors; i++) {
            Vendor vendor = new Vendor(ticketPool, i + 1);
            Thread vendorThread = new Thread(vendor, "Vendor-" + (i + 1));
            vendorThreads.add(vendorThread);
            vendorThread.start();
        }

        // Start customer threads
        for (int i = 0; i < customers; i++) {
            Customer customer = new Customer(ticketPool, i + 1);
            Thread customerThread = new Thread(customer, "Customer-" + (i + 1));
            customerThreads.add(customerThread);
            customerThread.start();
        }

        // Monitor the system for termination
        new Thread(() -> {
            while (!ticketPool.isTerminated()) {
                try {
                    Thread.sleep(1000); // Check every second
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // Stop all threads when the system terminates
            stopAllThreads();

            // Print final summary and notify WebSocket clients
            String summary = ticketPool.generateSummary(false); // Pass 'false' for natural termination
            System.out.println(summary);
            webSocketHandler.broadcastMessage(summary);
        }).start();

        return "System started with " + vendors + " vendors and " + customers + " customers.";
    }

    @PostMapping("/stop")
    public String stopSystem() {
        if (ticketPool == null) {
            return "System not started or already stopped.";
        }

        // Stop the system and broadcast a message
        ticketPool.stopSystem();
        webSocketHandler.broadcastMessage("[System] Manual stop triggered.");

        return "System stopped manually.";
    }

    @GetMapping("/status")
    public String getSystemStatus() {
        if (ticketPool == null) {
            return "System not configured.";
        }

        return "System Status: " +
                "\nTickets Sold: " + ticketPool.getTotalTicketsSold() +
                "\nTickets in Pool: " + ticketPool.getTicketsInPool() +
                "\nSystem Terminated: " + ticketPool.isTerminated();
    }

    private void stopAllThreads() {
        List<String> interruptedVendors = new ArrayList<>();
        List<String> interruptedCustomers = new ArrayList<>();

        // Interrupt vendor threads
        for (Thread thread : vendorThreads) {
            if (thread.getName().startsWith("Vendor")) {
                interruptedVendors.add(thread.getName());
                thread.interrupt();
            }
        }

        // Interrupt customer threads
        for (Thread thread : customerThreads) {
            if (thread.getName().startsWith("Customer")) {
                interruptedCustomers.add(thread.getName());
                thread.interrupt();
            }
        }

        vendorThreads.clear();
        customerThreads.clear();
    }
}
