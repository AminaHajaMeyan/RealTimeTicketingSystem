package com.amina.backend.controller;

import com.amina.backend.config.TicketConfig;
import com.amina.backend.service.TicketService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/configure")
    public String configureSystem(@RequestBody TicketConfig config) {
        ticketService.configureSystem(config);
        return "System configured successfully!";
    }

    @PostMapping("/start")
    public String startSystem() {
        ticketService.startSystem();
        return "System started.";
    }

    @PostMapping("/stop")
    public String stopSystem() {
        ticketService.stopSystem();
        return "System stopped.";
    }

    @PostMapping("/reset")
    public String resetSystem() {
        ticketService.resetSystem();
        return "System reset.";
    }

    @PostMapping("/status")
    public String getSystemStatus() {
        return ticketService.getSummary();
    }
}
