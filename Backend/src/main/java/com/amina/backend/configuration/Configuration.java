package com.amina.backend.configuration;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configuration {

    @NotNull
    @Min(1)
    @Value("${total.tickets:100}")
    private Integer totalTickets;

    @NotNull
    @Min(1)
    @Value("${max.ticket.capacity:50}")
    private Integer maxTicketCapacity;

    @NotNull
    @Min(1)
    @Value("${ticket.release.rate:10}")
    private Integer ticketReleaseRate;

    @NotNull
    @Min(1)
    @Value("${customer.retrieval.rate:5}")
    private Integer customerRetrievalRate;

    // Getters and Setters
    public Integer getTotalTickets() { return totalTickets; }
    public void setTotalTickets(Integer totalTickets) { this.totalTickets = totalTickets; }

    public Integer getMaxTicketCapacity() { return maxTicketCapacity; }
    public void setMaxTicketCapacity(Integer maxTicketCapacity) { this.maxTicketCapacity = maxTicketCapacity; }

    public Integer getTicketReleaseRate() { return ticketReleaseRate; }
    public void setTicketReleaseRate(Integer ticketReleaseRate) { this.ticketReleaseRate = ticketReleaseRate; }

    public Integer getCustomerRetrievalRate() { return customerRetrievalRate; }
    public void setCustomerRetrievalRate(Integer customerRetrievalRate) { this.customerRetrievalRate = customerRetrievalRate; }
}
