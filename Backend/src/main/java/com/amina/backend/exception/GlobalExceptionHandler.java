package com.amina.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for managing application-wide exceptions.
 * <p>
 * This class handles specific exceptions and provides consistent error responses
 * to the client. It uses Spring's {@code @ControllerAdvice} to intercept exceptions globally.
 * </p>
 *
 * @author Amina
 * @version 1.0
 * @since 2024-12-11
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors for method arguments annotated with {@code @Valid}.
     * <p>
     * Extracts field-specific error messages from {@link MethodArgumentNotValidException}
     * and returns them as a map of field names to error messages.
     * </p>
     *
     * @param ex The exception thrown during validation.
     * @return A {@link ResponseEntity} containing a map of field errors with {@code BAD_REQUEST} status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
