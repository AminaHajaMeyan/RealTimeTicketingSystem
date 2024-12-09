package com.amina.backend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class ConfigurationManager {
    private static final String CONFIG_FILE = "config.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public void saveConfig(Configuration config) {
        try {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(new File(CONFIG_FILE), config);
            System.out.println("Configuration saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving configuration: " + e.getMessage());
        }
    }

    public Configuration loadConfig() {
        try {
            return OBJECT_MAPPER.readValue(new File(CONFIG_FILE), Configuration.class);
        } catch (IOException e) {
            System.err.println("Configuration file not found or invalid.");
            return null;
        }
    }

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
