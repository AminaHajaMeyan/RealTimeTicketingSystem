package com.ticketingsystem.inputvalidation;

import java.util.Scanner;

public class InputValidation {

    public static int getPositiveInt(Scanner scanner, String prompt, String paramName) {
        while (true) {
            try {
                System.out.println(prompt);
                int input = Integer.parseInt(scanner.nextLine());
                validatePositive(paramName, input); // Validate positivity
                return input;
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a valid integer.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static int getBoundedPositiveInt(Scanner scanner, String prompt, String paramName, int max) {
        while (true) {
            try {
                System.out.println(prompt);
                int input = Integer.parseInt(scanner.nextLine());
                validateInput(paramName, input, 1, max); // Validate within bounds
                return input;
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a valid integer.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void validatePositive(String parameterName, int value) {
        if (value <= 0) {
            throw new IllegalArgumentException(parameterName + " must be positive.");
        }
    }

    public static void validateInput(String parameterName, int value, int min, int max) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(parameterName + " must be between " + min + " and " + max + ".");
        }
    }
}
