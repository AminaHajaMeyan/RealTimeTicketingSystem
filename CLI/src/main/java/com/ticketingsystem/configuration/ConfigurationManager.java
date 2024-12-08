package com.ticketingsystem.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigurationManager {
    private static final String CONFIG_FILE = "config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ConfigurationManager() {
        // Prevent instantiation
    }

    public static void saveConfig(Configuration config) {
        File configFile = new File(CONFIG_FILE);
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            System.err.println("Error saving configuration: " + e.getMessage());
        }
    }

    public static Configuration loadConfig() {
        File configFile = new File(CONFIG_FILE);
        try (FileReader reader = new FileReader(configFile)) {
            Configuration config = GSON.fromJson(reader, Configuration.class);
            validateConfig(config); // Validate values
            return config;
        } catch (IOException e) {
            System.err.println("Configuration file not found or invalid: " + configFile.getAbsolutePath());
            return null; // File not found or cannot be read
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid configuration: " + e.getMessage());
            return null; // Invalid values in configuration
        }
    }

    private static void validateConfig(Configuration config) {
        if (config == null) {
            throw new IllegalArgumentException("Configuration is null.");
        }
        if (config.getTotalTickets() <= 0) {
            throw new IllegalArgumentException("Total Tickets must be greater than 0.");
        }
        if (config.getMaxTicketCapacity() <= 0) {
            throw new IllegalArgumentException("Max Ticket Capacity must be greater than 0.");
        }
        if (config.getTicketReleaseRate() <= 0) {
            throw new IllegalArgumentException("Ticket Release Rate must be greater than 0.");
        }
        if (config.getCustomerRetrievalRate() <= 0) {
            throw new IllegalArgumentException("Customer Retrieval Rate must be greater than 0.");
        }
        // Adjusted validation logic
        if (config.getTotalTickets() <= config.getMaxTicketCapacity()) {
            throw new IllegalArgumentException("Total Tickets must be greater than Max Ticket Capacity.");
        }
    }
}

