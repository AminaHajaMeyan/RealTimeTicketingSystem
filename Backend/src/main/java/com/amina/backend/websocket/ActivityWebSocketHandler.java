package com.amina.backend.websocket;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A WebSocket handler to manage client connections and broadcast messages.
 * <p>
 * This handler maintains a thread-safe list of active WebSocket sessions,
 * enabling broadcasting of real-time messages to all connected clients.
 * </p>
 *
 * @author Amina
 * @version 1.0
 * @since 2024-12-11
 */
@Component
public class ActivityWebSocketHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    /**
     * Called after a WebSocket connection is established.
     *
     * @param session The established WebSocket session.
     */
    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        sessions.add(session);
        System.out.println("WebSocket connection established: " + session.getId());
    }

    /**
     * Called after a WebSocket connection is closed.
     *
     * @param session The closed WebSocket session.
     * @param status  The close status.
     */
    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        sessions.remove(session);
        System.out.println("WebSocket connection closed: " + session.getId());
    }

    /**
     * Handles text messages received from WebSocket clients.
     *
     * @param session The session from which the message was received.
     * @param message The received text message.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println("Received message from session " + session.getId() + ": " + message.getPayload());
    }

    /**
     * Broadcasts a message to all connected WebSocket clients.
     *
     * @param message The message to broadcast.
     */
    public void broadcastMessage(String message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    System.err.println("Error sending message to session " + session.getId() + ": " + e.getMessage());
                }
            } else {
                System.err.println("Skipping closed session: " + session.getId());
            }
        }
    }
}
