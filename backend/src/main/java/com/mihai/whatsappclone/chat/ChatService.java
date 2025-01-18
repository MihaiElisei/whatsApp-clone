package com.mihai.whatsappclone.chat;

import com.mihai.whatsappclone.user.User;
import com.mihai.whatsappclone.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for handling chat-related operations such as retrieving and creating chats.
 */
@Service
@RequiredArgsConstructor
public class ChatService {

    /**
     * Repository for interacting with the Chat data in the database.
     */
    private final ChatRepository chatRepository;

    /**
     * Mapper for converting Chat entities to response DTOs.
     */
    private final ChatMapper mapper;

    /**
     * Repository for interacting with User data in the database.
     */
    private final UserRepository userRepository;

    /**
     * Retrieves a list of chats for the currently authenticated user by their user ID.
     *
     * @param currentUser The authentication object of the currently logged-in user.
     * @return A list of ChatResponse DTOs representing the chats.
     */
    @Transactional(readOnly = true)
    public List<ChatResponse> getChatsByReceiverId(Authentication currentUser) {
        final String userId = currentUser.getName();

        // Fetch chats where the current user is the sender and map them to response objects
        return chatRepository.findChatsBySenderId(userId)
                .stream()
                .map(c -> mapper.toChatResponse(c, userId))
                .toList();
    }

    /**
     * Creates a new chat between a sender and a recipient. If a chat already exists between them,
     * it returns the existing chat's ID.
     *
     * @param senderId The public ID of the sender.
     * @param recipientId The public ID of the recipient.
     * @return The ID of the created or existing chat.
     */
    public String createChat(String senderId, String recipientId) {
        // Check if a chat already exists between the sender and recipient
        Optional<Chat> existingChat = chatRepository.findChatByReceiverAndSender(senderId, recipientId);

        if (existingChat.isPresent()) {
            return existingChat.get().getId(); // Return the existing chat ID
        }

        // Retrieve the sender from the repository, or throw an exception if not found
        User sender = userRepository.findByPublicId(senderId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + senderId + " not found"));

        // Retrieve the recipient from the repository, or throw an exception if not found
        User recipient = userRepository.findByPublicId(recipientId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + recipientId + " not found"));

        // Create a new chat entity
        Chat chat = new Chat();
        chat.setSender(sender);
        chat.setRecipient(recipient);

        // Save the new chat to the repository and return its ID
        Chat savedChat = chatRepository.save(chat);
        return savedChat.getId();
    }
}
