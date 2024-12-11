package com.amina.backend.ticket;

/**
 * Represents a ticket in the ticketing system.
 * <p>
 * Each ticket has a unique identifier, an event name, and a seat number.
 * This class is immutable and provides getters for all fields.
 * </p>
 *
 * @author Amina
 * @version 1.0
 * @since 2024-12-11
 */
public class Ticket {
    private final int ticketId;
    private final String eventName;
    private final int seatNumber;

    /**
     * Constructs a new {@code Ticket} with the specified details.
     *
     * @param ticketId   The unique identifier for the ticket.
     * @param eventName  The name of the event for which the ticket is issued.
     * @param seatNumber The seat number associated with the ticket.
     */
    public Ticket(int ticketId, String eventName, int seatNumber) {
        this.ticketId = ticketId;
        this.eventName = eventName;
        this.seatNumber = seatNumber;
    }

    /**
     * Gets the unique identifier of the ticket.
     *
     * @return The ticket ID.
     */
    public int getTicketId() {
        return ticketId;
    }

    /**
     * Gets the name of the event associated with the ticket.
     *
     * @return The event name.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Gets the seat number associated with the ticket.
     *
     * @return The seat number.
     */
    public int getSeatNumber() {
        return seatNumber;
    }

    /**
     * Returns a string representation of the ticket.
     *
     * @return A string representation of the ticket in the format:
     *         {@code Ticket{ticketId=1, eventName='Event Name', seatNumber=10}}.
     */
    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId=" + ticketId +
                ", eventName='" + eventName + '\'' +
                ", seatNumber=" + seatNumber +
                '}';
    }
}
