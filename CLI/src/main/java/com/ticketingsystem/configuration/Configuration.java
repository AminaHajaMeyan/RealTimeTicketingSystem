package com.ticketingsystem.configuration;

import java.io.Serializable;

public class Configuration implements Serializable {
    private final int totalTickets;
    private final int maxTicketCapacity;
    private final int ticketReleaseRate;
    private final int customerRetrievalRate;


    public Configuration(int totalTickets,int maxTicketCapacity, int ticketReleaseRate, int customerRetrievalRate) {
        this.totalTickets = totalTickets;
        this.maxTicketCapacity = maxTicketCapacity;
        this.ticketReleaseRate = ticketReleaseRate;
        this.customerRetrievalRate = customerRetrievalRate;

    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    @Override
    public String toString() {
        return String.format("Configuration [Total Tickets: %d, Max Capacity: %d, Release Rate: %d, Retrieval Rate: %d]",
                totalTickets, maxTicketCapacity, ticketReleaseRate, customerRetrievalRate);
    }

}

