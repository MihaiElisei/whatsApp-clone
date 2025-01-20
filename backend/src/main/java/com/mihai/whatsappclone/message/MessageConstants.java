package com.mihai.whatsappclone.message;

/**
 * Utility class for storing constants related to the Message entity.
 */
public class MessageConstants {

    // Named query for finding messages by chat ID.
    public static final String FIND_MESSAGES_BY_CHAT_ID = "Messages.findMessagesByChatId";

    // Named query for updating the state of messages in a specific chat.
    public static final String SET_MESSAGES_TO_SEEN_BY_CHAT = "Messages.setMessagesToSeenByChat";

    // Private constructor to prevent instantiation.
    private MessageConstants() {}
}
