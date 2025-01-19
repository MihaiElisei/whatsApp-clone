package com.mihai.whatsappclone.message;

import com.mihai.whatsappclone.chat.Chat;
import com.mihai.whatsappclone.chat.ChatRepository;
import com.mihai.whatsappclone.file.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok annotation to generate a constructor with required (final) fields.
public class MessageService {

    private final MessageRepository messageRepository; // Repository for database operations on messages.
    private final ChatRepository chatRepository; // Repository for database operations on chats.
    private final MessageMapper mapper; // Utility to map Message entities to DTOs.
    private final FileService fileService; // Service for handling file operations (e.g., saving media files).

    /**
     * Saves a message in the specified chat.
     *
     * @param messageRequest Object containing the details of the message to be saved.
     * @throws EntityNotFoundException if the chat with the given ID does not exist.
     */
    public void saveMessage(MessageRequest messageRequest) {
        // Find the chat by its ID or throw an exception if not found.
        Chat chat = chatRepository.findById(messageRequest.getChatId())
                .orElseThrow(() -> new EntityNotFoundException("Chat with id " + messageRequest.getChatId() + " not found"));

        // Create a new Message entity and populate its fields with data from the request.
        Message message = new Message();
        message.setContent(messageRequest.getContent());
        message.setChat(chat);
        message.setSenderId(messageRequest.getSenderId());
        message.setRecipientId(messageRequest.getRecipientId());
        message.setType(messageRequest.getType());
        message.setState(MessageState.SENT);

        // Save the message to the database.
        messageRepository.save(message);
    }

    /**
     * Retrieves all messages in a chat by chat ID.
     *
     * @param chatId The ID of the chat whose messages are to be retrieved.
     * @return A list of MessageResponse DTOs.
     */
    public List<MessageResponse> findChatMessages(String chatId) {
        // Fetch messages by chat ID, map them to DTOs, and return as a list.
        return messageRepository.findMessagesByChatId(chatId)
                .stream()
                .map(mapper::toMessageResponse) // Use the mapper to convert entities to DTOs.
                .toList();
    }

    /**
     * Marks all messages in a chat as "seen".
     *
     * @param chatId The ID of the chat where messages need to be marked as seen.
     * @param authentication The current authenticated user's details.
     * @throws EntityNotFoundException if the chat with the given ID does not exist.
     */
    @Transactional // Ensures this operation is performed within a database transaction.
    public void setMessagesToSeen(String chatId, Authentication authentication) {
        // Find the chat by its ID or throw an exception if not found.
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat with id " + chatId + " not found"));

        //final String recipientId = getRecipientId(chat, authentication);

        // Update the state of messages in the chat to be SEEN.
        messageRepository.setMessagesToSeenByChatId(chatId, MessageState.SEEN);
    }

    /**
     * Uploads a media message to a chat.
     *
     * @param chatId The ID of the chat where the media message will be added.
     * @param file The media file to be uploaded.
     * @param authentication The current authenticated user's details.
     * @throws EntityNotFoundException if the chat with the given ID does not exist.
     */
    public void uploadMediaMessage(String chatId, MultipartFile file, Authentication authentication) {
        // Find the chat by its ID or throw an exception if not found.
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat with id " + chatId + " not found"));

        // Determine the sender and recipient IDs based on the current authenticated user.
        final String senderId = getSenderId(chat, authentication);
        final String recipientId = getRecipientId(chat, authentication);

        // Save the uploaded file and get its file path.
        final String filePath = fileService.saveFile(file, senderId);

        // Create a new Message entity for the uploaded media.
        Message message = new Message();
        message.setChat(chat);
        message.setSenderId(senderId);
        message.setRecipientId(recipientId);
        message.setType(MessageType.IMAGE); // Message type is set to IMAGE for media files.
        message.setState(MessageState.SENT); // Initial state of the message is SENT.
        message.setMediaFilePath(filePath); // Path to the uploaded media file.

        // Save the media message to the database.
        messageRepository.save(message);
    }

    /**
     * Determines the sender ID for a chat based on the authenticated user.
     *
     * @param chat The chat entity.
     * @param authentication The current authenticated user's details.
     * @return The sender ID.
     */
    private String getSenderId(Chat chat, Authentication authentication) {
        // Return the sender's ID if it matches the authenticated user; otherwise, return the recipient's ID.
        if (chat.getSender().getId().equals(authentication.getName())) {
            return chat.getSender().getId();
        }
        return chat.getRecipient().getId();
    }

    /**
     * Determines the recipient ID for a chat based on the authenticated user.
     *
     * @param chat The chat entity.
     * @param authentication The current authenticated user's details.
     * @return The recipient ID.
     */
    private String getRecipientId(Chat chat, Authentication authentication) {
        // Return the recipient's ID if the sender matches the authenticated user; otherwise, return the sender's ID.
        if (chat.getSender().getId().equals(authentication.getName())) {
            return chat.getRecipient().getId();
        }
        return chat.getSender().getId();
    }
}
