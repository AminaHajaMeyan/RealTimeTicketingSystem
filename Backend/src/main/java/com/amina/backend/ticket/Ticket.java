package com.amina.backend.ticket;

public class Ticket {
    private final int ticketId;
    private final String eventName;
    private final int seatNumber;

    public Ticket(int ticketId, String eventName, int seatNumber) {
        this.ticketId = ticketId;
        this.eventName = eventName;
        this.seatNumber = seatNumber;
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getEventName() {
        return eventName;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", eventName='" + eventName + '\'' +
                ", seatNumber=" + seatNumber +
                '}';
    }
}
