package com.amina.backend.config;

public class TicketActivity {
    private String message;

    public TicketActivity(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
