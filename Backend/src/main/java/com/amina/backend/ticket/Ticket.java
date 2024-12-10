package com.amina.backend.ticket;

public class Ticket {
    private final int ticketId;
    private final String seatNumber;
    private final String eventName = "Concert Event";

    public Ticket(int ticketId, String seatNumber) {
        this.ticketId = ticketId;
        this.seatNumber = seatNumber;
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getEventName() {
        return eventName;
    }

    @Override
    public String toString() {
        return "Ticket[ID=" + ticketId + ", Seat='" + seatNumber + "', Event='" + eventName + "']";
    }
}

