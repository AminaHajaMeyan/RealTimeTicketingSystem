package com.amina.backend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Manages the configuration settings for the ticketing system.
 * <p>
 * This class provides methods to save, load, print, and clear the configuration settings
 * stored in a JSON file. It uses the {@link ObjectMapper} for JSON serialization and deserialization.
 * </p>
 *
 * @author Amina
 * @version 1.0
 * @since 2024-12-11
 */
@Component
public class ConfigurationManager {

    /**
     * The name of the configuration file.
     */
    private static final String CONFIG_FILE = "config.json";

    /**
     * ObjectMapper instance for handling JSON operations.
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Saves the given configuration to a JSON file.
     *
     * @param config The {@link Configuration} object to be saved.
     */
    public void saveConfig(Configuration config) {
        try {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(new File(CONFIG_FILE), config);
            System.out.println("Configuration saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving configuration: " + e.getMessage());
        }
    }

    /**
     * Loads the configuration from the JSON file.
     *
     * @return The loaded {@link Configuration} object, or {@code null} if the file is not found or invalid.
     */
    public Configuration loadConfig() {
        try {
            return OBJECT_MAPPER.readValue(new File(CONFIG_FILE), Configuration.class);
        } catch (IOException e) {
            System.err.println("Configuration file not found or invalid.");
            return null;
        }
    }

    /**
     * Prints a summary of the given configuration to the console.
     *
     * @param config The {@link Configuration} object whose summary is to be printed.
     */
    public void printConfigSummary(Configuration config) {
        System.out.printf("""
                === Configuration Summary ===
                Total Tickets: %d
                Max Ticket Capacity: %d
                Ticket Release Rate: %d
                Customer Retrieval Rate: %d
                =============================\n""",
                config.getTotalTickets(),
                config.getMaxTicketCapacity(),
                config.getTicketReleaseRate(),
                config.getCustomerRetrievalRate());
    }
}
