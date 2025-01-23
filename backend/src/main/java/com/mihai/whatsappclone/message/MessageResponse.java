package com.mihai.whatsappclone.message;

import lombok.*;

import java.time.LocalDateTime;

/**
 * A DTO (Data Transfer Object) representing the structure of a message response.
 * Used for transferring message data from the server to the client.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {

    /**
     * The unique identifier of the message.
     */
    private Long id;

    /**
     * The content of the message.
     */
    private String content;

    /**
     * The type of the message (e.g., text, image, video).
     */
    private MessageType type;

    /**
     * The state of the message (e.g., sent, delivered, read).
     */
    private MessageState state;

    /**
     * The ID of the user who sent the message.
     */
    private String senderId;

    /**
     * The ID of the user who received the message.
     */
    private String recipientId;

    /**
     * The timestamp when the message was created.
     */
    private LocalDateTime createdAt;

    /**
     * The media content of the message (if any), stored as a byte array.
     */
    private byte[] media;
}
