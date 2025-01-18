package com.mihai.whatsappclone.chat;

/**
 * Utility class for storing constants related to the Chat entity.
 */
public class ChatConstants {

    // Named query for finding chats by sender ID.
    public static final String FIND_CHAT_BY_SENDER_ID = "Chat.findChatBySenderId";

    // Named query for finding chats by sender ID and receiver ID.
    public static final String FIND_CHAT_BY_SENDER_ID_AND_RECEIVER_ID = "Chat.findChatBySenderIdAndReceiverId";

    // Private constructor to prevent instantiation of the class.
    private ChatConstants() {}
}
