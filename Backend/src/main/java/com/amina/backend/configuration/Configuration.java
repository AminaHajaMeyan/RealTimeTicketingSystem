package com.amina.backend.configuration;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Represents the configuration settings for the ticketing system.
 * <p>
 * This class is responsible for holding the configurable properties of the system,
 * such as the total number of tickets, maximum ticket capacity, ticket release rate,
 * and customer retrieval rate.
 * </p>
 * <p>
 * The values are injected using Spring's {@code @Value} annotation and validated using
 * Jakarta Bean Validation constraints.
 * </p>
 *
 * @author Amina
 * @version 1.0
 * @since 2024-12-11
 */
@Component
public class Configuration {

    /**
     * The total number of tickets available in the system.
     * <p>
     * Must be a positive integer and is initialized with a default value of 100.
     * </p>
     */
    @NotNull
    @Min(1)
    @Value("${total.tickets:100}")
    private Integer totalTickets;

    /**
     * The maximum number of tickets that can be held in the pool at any given time.
     * <p>
     * Must be a positive integer and is initialized with a default value of 50.
     * </p>
     */
    @NotNull
    @Min(1)
    @Value("${max.ticket.capacity:50}")
    private Integer maxTicketCapacity;

    /**
     * The rate at which tickets are released into the pool.
     * <p>
     * Must be a positive integer and is initialized with a default value of 10.
     * </p>
     */
    @NotNull
    @Min(1)
    @Value("${ticket.release.rate:10}")
    private Integer ticketReleaseRate;

    /**
     * The rate at which customers retrieve tickets from the pool.
     * <p>
     * Must be a positive integer and is initialized with a default value of 5.
     * </p>
     */
    @NotNull
    @Min(1)
    @Value("${customer.retrieval.rate:5}")
    private Integer customerRetrievalRate;

    // Getters and Setters

    /**
     * Gets the total number of tickets.
     *
     * @return The total number of tickets.
     */
    public Integer getTotalTickets() {
        return totalTickets;
    }

    /**
     * Sets the total number of tickets.
     *
     * @param totalTickets The total number of tickets to set.
     */
    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }

    /**
     * Gets the maximum ticket capacity.
     *
     * @return The maximum ticket capacity.
     */
    public Integer getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    /**
     * Sets the maximum ticket capacity.
     *
     * @param maxTicketCapacity The maximum ticket capacity to set.
     */
    public void setMaxTicketCapacity(Integer maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
    }

    /**
     * Gets the ticket release rate.
     *
     * @return The ticket release rate.
     */
    public Integer getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    /**
     * Sets the ticket release rate.
     *
     * @param ticketReleaseRate The ticket release rate to set.
     */
    public void setTicketReleaseRate(Integer ticketReleaseRate) {
        this.ticketReleaseRate = ticketReleaseRate;
    }

    /**
     * Gets the customer retrieval rate.
     *
     * @return The customer retrieval rate.
     */
    public Integer getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    /**
     * Sets the customer retrieval rate.
     *
     * @param customerRetrievalRate The customer retrieval rate to set.
     */
    public void setCustomerRetrievalRate(Integer customerRetrievalRate) {
        this.customerRetrievalRate = customerRetrievalRate;
    }
}
