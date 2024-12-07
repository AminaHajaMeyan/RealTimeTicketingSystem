package com.amina.backend.controller;

import com.amina.backend.config.TicketConfig;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TicketController {

    @PostMapping("/configure")
    public String configureSystem(@RequestBody TicketConfig config) {
        return "System configured with: " +
                "Total Tickets = " + config.getTotalTickets() +
                ", Max Capacity = " + config.getMaxCapacity() +
                ", Release Rate = " + config.getReleaseRate() +
                ", Retrieval Rate = " + config.getRetrievalRate();
    }

    @PostMapping("/start")
    public String startSystem() {
        return "System started.";
    }

    @PostMapping("/stop")
    public String stopSystem() {
        return "System stopped.";
    }

    @PostMapping("/reset")
    public String resetSystem() {
        return "System reset.";
    }
}
