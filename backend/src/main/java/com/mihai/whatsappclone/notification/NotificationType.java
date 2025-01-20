package com.mihai.whatsappclone.notification;

/**
 * Enum representing the different types of notifications that can be sent in the application.
 */
public enum NotificationType {

    SEEN,    // Notification type for marking messages as seen.
    MESSAGE, // Notification type for a new text message.
    IMAGE,   // Notification type for a new image message.
    AUDIO,   // Notification type for a new audio message.
    VIDEO    // Notification type for a new video message.
}
