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
    private boolean isRunning = false; // New flag to track system state

    public TicketSystemController(ConfigurationManager configManager, ActivityWebSocketHandler webSocketHandler) {
        this.configManager = configManager;
        this.webSocketHandler = webSocketHandler;
    }

    @PostMapping("/configure")
    public ResponseEntity<String> configureSystem(@RequestBody @Valid Configuration config) {
        if (isRunning) {
            return ResponseEntity.badRequest().body("System is already running. Stop the system before reconfiguring.");
        }

        if (config.getTotalTickets() <= 0) {
            return ResponseEntity.badRequest().body("Total tickets must be greater than 0.");
        }

        if (config.getMaxTicketCapacity() <= 0) {
            return ResponseEntity.badRequest().body("Max ticket capacity must be greater than 0.");
        }

        if (config.getTotalTickets() < config.getMaxTicketCapacity()) {
            return ResponseEntity.badRequest().body("Total tickets must be greater than or equal to max ticket capacity.");
        }

        configManager.saveConfig(config);
        configManager.printConfigSummary(config);

        this.ticketPool = new TicketPool(config.getMaxTicketCapacity(), config.getTotalTickets(), webSocketHandler);

        return ResponseEntity.ok("System configured successfully.");
    }

    @PostMapping("/start")
    public ResponseEntity<String> startSystem() {
        if (ticketPool == null) {
            return ResponseEntity.badRequest().body("System not configured. Please configure the system before starting.");
        }

        if (isRunning) {
            return ResponseEntity.badRequest().body("System is already running.");
        }

        isRunning = true;

        startThreads(); // Default thread counts: 5 vendors, 10 customers
        monitorSystemTermination();

        return ResponseEntity.ok("System started successfully.");
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopSystem() {
        if (!isRunning) {
            return ResponseEntity.badRequest().body("System is not running.");
        }

        ticketPool.stopSystem();
        stopAllThreads();
        webSocketHandler.broadcastMessage("[System] Manual stop triggered.");
        isRunning = false;

        return ResponseEntity.ok("System stopped successfully.");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetSystem() {
        if (isRunning) {
            stopSystem(); // Stop the system before resetting
        }

        ticketPool = null;
        webSocketHandler.broadcastMessage("[System] Reset triggered. System is ready for reconfiguration.");
        isRunning = false;

        return ResponseEntity.ok("System runtime state has been reset. Configuration remains intact.");
    }

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

    private void monitorSystemTermination() {
        new Thread(() -> {
            try {
                while (!ticketPool.isTerminated()) {
                    Thread.sleep(1000); // Check every second
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
