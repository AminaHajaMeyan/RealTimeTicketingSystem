package com.amina.backend.config;

public class TicketConfig {
    private int totalTickets;
    private int maxCapacity;
    private int releaseRate;
    private int retrievalRate;

    public TicketConfig() {}

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getReleaseRate() {
        return releaseRate;
    }

    public void setReleaseRate(int releaseRate) {
        this.releaseRate = releaseRate;
    }

    public int getRetrievalRate() {
        return retrievalRate;
    }

    public void setRetrievalRate(int retrievalRate) {
        this.retrievalRate = retrievalRate;
    }

    public void validate() {
        if (totalTickets <= 0 || maxCapacity <= 0 || releaseRate <= 0 || retrievalRate <= 0) {
            throw new IllegalArgumentException("All rates and capacities must be greater than 0.");
        }
        if (maxCapacity > totalTickets) {
            throw new IllegalArgumentException("Max capacity cannot exceed total tickets.");
        }
        if (releaseRate > maxCapacity) {
            throw new IllegalArgumentException("Release rate cannot exceed max capacity.");
        }
        if (retrievalRate > maxCapacity) {
            throw new IllegalArgumentException("Retrieval rate cannot exceed max capacity.");
        }
    }
}
