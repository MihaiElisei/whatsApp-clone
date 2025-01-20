package com.mihai.whatsappclone.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Chat entities.
 * It extends JpaRepository to provide standard CRUD operations and includes custom query methods
 * for retrieving chats based on specific criteria.
 */
public interface ChatRepository extends JpaRepository<Chat, String> {

    /**
     * Retrieves a list of chats where the given user is the sender.
     *
     * @param userId The ID of the sender whose chats are to be retrieved.
     * @return A list of Chat entities where the user is the sender.
     */
    @Query(name = ChatConstants.FIND_CHAT_BY_SENDER_ID)
    List<Chat> findChatsBySenderId(@Param("senderId") String userId);

    /**
     * Finds a chat between a specific sender and recipient, if it exists.
     *
     * @param senderId The ID of the sender.
     * @param recipientId The ID of the recipient.
     * @return An Optional containing the Chat entity if found, or empty if no such chat exists.
     */
    @Query(name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER_ID)
    Optional<Chat> findChatByReceiverAndSender(@Param("senderId") String senderId, @Param("recipientId") String recipientId);
}
