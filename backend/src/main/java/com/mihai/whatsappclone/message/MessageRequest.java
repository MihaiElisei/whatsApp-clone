package com.mihai.whatsappclone.message;

import lombok.*;

/**
 * A DTO (Data Transfer Object) representing the structure of a message request.
 * Used for transferring message data between the client and the server.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequest {

    /**
     * The content of the message.
     */
    private String content;

    /**
     * The ID of the user sending the message.
     */
    private String senderId;

    /**
     * The ID of the user receiving the message.
     */
    private String recipientId;

    /**
     * The type of the message (e.g., text, image, video).
     */
    private MessageType type;

    /**
     * The ID of the chat to which the message belongs.
     */
    private String chatId;
}
