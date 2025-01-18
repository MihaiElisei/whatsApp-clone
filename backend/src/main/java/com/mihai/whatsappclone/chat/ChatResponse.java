package com.mihai.whatsappclone.chat;

import lombok.*;

import java.time.LocalDateTime;

/**
 * A Data Transfer Object (DTO) representing the response structure for chat-related operations.
 * This class encapsulates details about a chat, including metadata such as the last message, unread count,
 * and the online status of the recipient.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponse {

    /**
     * The unique identifier for the chat.
     */
    private String id;

    /**
     * The display name associated with the chat (e.g., recipient's name).
     */
    private String name;

    /**
     * The number of unread messages in the chat.
     */
    private long unreadCount;

    /**
     * The content of the last message in the chat.
     */
    private String lastMessage;

    /**
     * The timestamp of the last message in the chat.
     */
    private LocalDateTime lastMessageTime;

    /**
     * Indicates whether the recipient is currently online.
     */
    private boolean isRecipientOnline;

    /**
     * The unique identifier of the sender in the chat.
     */
    private String senderId;

    /**
     * The unique identifier of the recipient in the chat.
     */
    private String recipientId;
}
