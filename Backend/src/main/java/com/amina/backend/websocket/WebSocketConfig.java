package com.amina.backend.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket configuration class for enabling and registering WebSocket handlers.
 * <p>
 * This configuration sets up the endpoint for WebSocket communication and links it to the
 * {@link ActivityWebSocketHandler} for handling real-time updates.
 * </p>
 *
 * @author Amina
 * @version 1.0
 * @since 2024-12-11
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ActivityWebSocketHandler activityWebSocketHandler;

    /**
     * Constructor to inject the {@link ActivityWebSocketHandler}.
     *
     * @param activityWebSocketHandler The handler for WebSocket events.
     */
    public WebSocketConfig(ActivityWebSocketHandler activityWebSocketHandler) {
        this.activityWebSocketHandler = activityWebSocketHandler;
    }

    /**
     * Registers WebSocket handlers with the specified endpoint and configuration.
     *
     * @param registry The {@link WebSocketHandlerRegistry} for registering handlers.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(activityWebSocketHandler, "/live-updates")
                .setAllowedOrigins("*"); // Allow connections from all origins
    }
}
