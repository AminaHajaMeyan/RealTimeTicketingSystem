package com.ticketingsystem;

import com.ticketingsystem.logger.Logger;
import com.ticketingsystem.configuration.Configuration;
import com.ticketingsystem.configuration.ConfigurationManager;
import com.ticketingsystem.inputvalidation.InputValidation;
import com.ticketingsystem.ticket.TicketPool;
import com.ticketingsystem.user.Customer;
import com.ticketingsystem.user.Vendor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static List<Thread> vendorThreads = new ArrayList<>();
    private static List<Thread> customerThreads = new ArrayList<>();
    private static TicketPool ticketPool;
    private static volatile boolean stopRequested = false; // Flag to handle stop command

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Event Ticketing System Configuration!");
        System.out.println("Managing tickets for a single event: 'Concert Event'");

        Configuration configuration = null;

        while (true) {
            System.out.println("Enter 'load' to load existing configuration, 'new' to configure a new system:");
            String setupCommand = scanner.nextLine().trim().toLowerCase();
            if ("load".equals(setupCommand)) {
                configuration = ConfigurationManager.loadConfig();
                if (configuration == null) {
                    System.out.println("Failed to load configuration or invalid values detected. Please configure manually.");
                    configuration = configureSystem(scanner);
                    ConfigurationManager.saveConfig(configuration); // Save the new configuration
                } else {
                    System.out.println("Configuration loaded successfully:\n" + configuration);
                }
                break;

            } else if ("new".equals(setupCommand)) {
                configuration = configureSystem(scanner);
                ConfigurationManager.saveConfig(configuration); // Save the new configuration
                System.out.println("Configuration saved successfully.");
                break;
            } else {
                System.out.println("Invalid command. Please enter 'load' or 'new'.");
            }
        }

        while (true) {
            System.out.println("Enter 'start' to begin operations or 'stop' to exit:");
            String command = scanner.nextLine().trim().toLowerCase();
            if ("start".equals(command)) {
                Logger.log("System started with configuration: " + configuration);
                startOperations(configuration, scanner);
                break;
            } else if ("stop".equals(command)) {
                Logger.log("System terminated by user.");
                break;
            } else {
                System.out.println("Invalid command. Enter 'start' or 'stop'.");
            }
        }

        scanner.close();
    }

    private static Configuration configureSystem(Scanner scanner) {
        System.out.println("Configuring the system manually.");

        int totalTickets = InputValidation.getPositiveInt(scanner,
                "Enter total number of tickets available: ",
                "Total Tickets");

        int maxTicketCapacity = InputValidation.getBoundedPositiveInt(scanner,
                "Enter maximum ticket capacity: ",
                "Max Ticket Capacity",
                totalTickets);

        int ticketReleaseRate = InputValidation.getBoundedPositiveInt(scanner,
                "Enter ticket release rate: ",
                "Ticket Release Rate",
                maxTicketCapacity);

        int customerRetrievalRate = InputValidation.getBoundedPositiveInt(scanner,
                "Enter customer retrieval rate: ",
                "Customer Retrieval Rate",
                maxTicketCapacity);

        System.out.println("Configuration completed successfully.");
        return new Configuration(totalTickets, maxTicketCapacity, ticketReleaseRate, customerRetrievalRate);
    }

    private static void startOperations(Configuration configuration, Scanner scanner) {

        // Initialize the TicketPool
        ticketPool = new TicketPool(configuration.getMaxTicketCapacity(), configuration.getTotalTickets());

        // Initialize vendors
        for (int i = 0; i < 5; i++) { // Example: 5 vendors
            Vendor vendor = new Vendor(ticketPool, i + 1);
            Thread vendorThread = new Thread(vendor, "Vendor-" + (i + 1));
            vendorThreads.add(vendorThread);
            vendorThread.start();
        }

        // Initialize customers
        for (int i = 0; i < 10; i++) { // Match customers to total tickets
            Customer customer = new Customer(ticketPool, i + 1, configuration.getCustomerRetrievalRate());
            Thread customerThread = new Thread(customer, "Customer-" + (i + 1));
            customerThreads.add(customerThread);
            customerThread.start();
        }

        // Start the stop command listener
        new Thread(() -> {
            while (!stopRequested) {
                if (scanner.nextLine().trim().equalsIgnoreCase("x")) {
                    stopRequested = true;
                    Logger.log("Stop command received. Terminating the system...");
                    stopOperations();
                }
            }
        }, "Stop-Command-Listener").start();

        // Monitoring thread for real-time logging
        new Thread(() -> {
            while (!stopRequested && !ticketPool.isTerminated()) {
                try {
                    Thread.sleep(2000); // Log every 2 seconds
                    Logger.log(ticketPool.getRealTimeStatus());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Monitoring-Thread").start();
    }

    private static void stopOperations() {
        for (Thread thread : vendorThreads) {
            thread.interrupt();
        }
        vendorThreads.clear();

        for (Thread thread : customerThreads) {
            thread.interrupt();
        }
        customerThreads.clear();

        // Log final status
        Logger.log("Final Status: " + ticketPool.getRealTimeStatus());
        Logger.log("All threads stopped. System terminated gracefully.");
    }
}

