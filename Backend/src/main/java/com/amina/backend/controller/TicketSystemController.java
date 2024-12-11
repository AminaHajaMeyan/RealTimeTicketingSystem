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

        this.ticketPool = new TicketPool(config.getMaxTicketCapacity(), config.getTotalTickets(), webSocketHandler);

        return ResponseEntity.ok("System configured successfully.");
    }

    @PostMapping("/start")
    public String startSystem() {
        if (ticketPool == null) {
            return "System not configured. Please configure the system before starting.";
        }

        startThreads(5, 10); // Default thread counts: 5 vendors, 10 customers

        monitorSystemTermination();

        return "System started successfully.";
    }

    @PostMapping("/stop")
    public String stopSystem() {
        if (ticketPool == null) {
            return "System not started or already stopped.";
        }

        ticketPool.stopSystem();
        stopAllThreads();
        webSocketHandler.broadcastMessage("[System] Manual stop triggered.");

        return "System stopped successfully.";
    }

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

    private void startThreads(int vendorCount, int customerCount) {
        for (int i = 0; i < vendorCount; i++) {
            Vendor vendor = new Vendor(ticketPool, i + 1);
            Thread vendorThread = new Thread(vendor, "Vendor-" + (i + 1));
            vendorThreads.add(vendorThread);
            vendorThread.start();
        }

        for (int i = 0; i < customerCount; i++) {
            Customer customer = new Customer(ticketPool, i + 1);
            Thread customerThread = new Thread(customer, "Customer-" + (i + 1));
            customerThreads.add(customerThread);
            customerThread.start();
        }
    }

    private void monitorSystemTermination() {
        new Thread(() -> {
            while (!ticketPool.isTerminated()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            stopAllThreads();

            String summary = ticketPool.generateSummary(false);
            System.out.println(summary);
            webSocketHandler.broadcastMessage(summary);
        }).start();
    }

    private void stopAllThreads() {
        vendorThreads.forEach(Thread::interrupt);
        customerThreads.forEach(Thread::interrupt);

        vendorThreads.clear();
        customerThreads.clear();
    }
}
