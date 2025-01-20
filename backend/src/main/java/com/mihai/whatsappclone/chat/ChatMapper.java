package com.mihai.whatsappclone.chat;

import org.springframework.stereotype.Service;

/**
 * Service class responsible for mapping Chat entities to ChatResponse DTOs.
 * This helps to decouple the entity model from the response structure used in the API.
 */
@Service
public class ChatMapper {

    /**
     * Maps a Chat entity to a ChatResponse DTO.
     *
     * @param c The Chat entity to be mapped.
     * @param senderId The ID of the sender to determine context-specific information.
     * @return A ChatResponse object containing the mapped data.
     */
    public ChatResponse toChatResponse(Chat c, String senderId) {
        return ChatResponse.builder()
                .id(c.getId()) // The unique ID of the chat.
                .name(c.getChatName(senderId)) // The name of the chat, determined by the sender's context.
                .unreadCount(c.getUnreadMessages(senderId)) // The number of unread messages for the sender.
                .lastMessage(c.getLastMessage()) // The content of the last message in the chat.
                .isRecipientOnline(c.getRecipient().isOnline()) // Whether the recipient is currently online.
                .senderId(c.getSender().getId()) // The ID of the sender.
                .recipientId(c.getRecipient().getId()) // The ID of the recipient.
                .build();
    }
}
