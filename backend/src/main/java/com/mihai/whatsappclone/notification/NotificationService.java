package com.mihai.whatsappclone.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service for handling and sending WebSocket notifications to users.
 * Utilizes Spring's SimpMessagingTemplate to send notifications over WebSocket.
 */
@Service // Marks this class as a Spring-managed service component.
@RequiredArgsConstructor // Generates a constructor for all final fields (dependency injection).
@Slf4j // Enables logging using the SLF4J framework.
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate; // Template for sending WebSocket messages.

    /**
     * Sends a WebSocket notification to a specific user.
     *
     * @param userId The ID of the user to receive the notification.
     * @param notification The notification payload to be sent.
     */
    public void sendNotification(String userId, Notification notification) {
        // Log the notification details for debugging purposes.
        log.info("Sending WebSocket notification to {} with payload {}", userId, notification);

        // Use the messaging template to send the notification to the specified user.
        messagingTemplate.convertAndSendToUser(
                userId, // The destination user ID.
                "/chat", // The WebSocket destination (subscribed topic).
                notification // The notification payload to be sent.
        );
    }
}
