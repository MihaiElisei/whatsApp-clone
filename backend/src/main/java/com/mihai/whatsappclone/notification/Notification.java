package com.mihai.whatsappclone.notification;

import com.mihai.whatsappclone.message.MessageType;
import lombok.*;

/**
 * Represents a notification in the application.
 * Notifications are used to inform users about new messages, message states, or other events.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // Enables the builder pattern for creating Notification objects.
public class Notification {

    private String chatId; // The ID of the chat related to the notification.
    private String content; // The content of the notification (e.g., text from a message).
    private String senderId; // The ID of the user who sent the message or triggered the notification.
    private String recipientId; // The ID of the user who will receive the notification.
    private String chatName; // The name of the chat associated with the notification.
    private MessageType messageType; // The type of the message (e.g., TEXT, IMAGE) triggering the notification.
    private NotificationType type; // The type of notification (e.g., MESSAGE, SEEN, TYPING).
    private byte[] media; // Optional media data included with the notification (e.g., images or files).
}
