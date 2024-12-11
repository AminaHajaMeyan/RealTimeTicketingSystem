package com.amina.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Real-Time Ticketing System Backend Application.
 * <p>
 * This class initializes the Spring Boot application, enabling the configuration,
 * component scanning, and auto-configuration of the backend services.
 * </p>
 *
 * <p>
 * Use this class to start the backend server for handling ticketing operations,
 * WebSocket communications, and API requests.
 * </p>
 *
 * @author Amina
 * @version 1.0
 * @since 2024-12-11
 */
@SpringBootApplication
public class BackendApplication {

    /**
     * Main method to launch the Spring Boot application.
     *
     * @param args Command-line arguments for the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        System.out.println("Backend Application is running...");
    }
}
