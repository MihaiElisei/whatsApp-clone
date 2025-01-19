package com.mihai.whatsappclone.message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for managing Message entities.
 * Extends JpaRepository to provide basic CRUD operations.
 * Includes custom query methods for chat-specific message retrieval and updates.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Retrieves all messages associated with a specific chat ID.
     * Messages are sorted by their creation date as defined in the named query.
     *
     * @param chatId The ID of the chat.
     * @return A list of Message entities belonging to the specified chat.
     */
    @Query(name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID)
    List<Message> findMessagesByChatId(@Param("chatId") String chatId);

    /**
     * Updates the state of all messages within a specific chat to the provided new state.
     * This operation modifies the database directly.
     *
     * @param chatId The ID of the chat whose messages are to be updated.
     * @param state  The new state to set for the messages.
     */
    @Query(name = MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT)
    @Modifying // Indicates this query modifies the database.
    void setMessagesToSeenByChatId(@Param("chatId") String chatId, @Param("newState") MessageState state);

}
