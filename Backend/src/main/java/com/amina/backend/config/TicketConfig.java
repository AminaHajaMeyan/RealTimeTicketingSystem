package com.amina.backend.config;

public class TicketConfig {
    private int totalTickets;
    private int maxCapacity;
    private int releaseRate;
    private int retrievalRate;

    public TicketConfig() {
        // Default constructor
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        if (totalTickets <= 0) {
            throw new IllegalArgumentException("Total tickets must be greater than 0.");
        }
        this.totalTickets = totalTickets;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Max capacity must be greater than 0.");
        }
        this.maxCapacity = maxCapacity;
    }

    public int getReleaseRate() {
        return releaseRate;
    }

    public void setReleaseRate(int releaseRate) {
        if (releaseRate <= 0) {
            throw new IllegalArgumentException("Release rate must be greater than 0.");
        }
        this.releaseRate = releaseRate;
    }

    public int getRetrievalRate() {
        return retrievalRate;
    }

    public void setRetrievalRate(int retrievalRate) {
        if (retrievalRate <= 0) {
            throw new IllegalArgumentException("Retrieval rate must be greater than 0.");
        }
        this.retrievalRate = retrievalRate;
    }

    public void validate() {
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
