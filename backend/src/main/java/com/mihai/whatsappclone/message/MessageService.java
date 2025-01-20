package com.mihai.whatsappclone.message;

import com.mihai.whatsappclone.chat.Chat;
import com.mihai.whatsappclone.chat.ChatRepository;
import com.mihai.whatsappclone.file.FileService;
import com.mihai.whatsappclone.file.FileUtils;
import com.mihai.whatsappclone.notification.Notification;
import com.mihai.whatsappclone.notification.NotificationService;
import com.mihai.whatsappclone.notification.NotificationType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service class for managing messages in the application.
 * Handles operations such as saving messages, retrieving messages, updating message states, and uploading media messages.
 */
@Service
@RequiredArgsConstructor // Lombok annotation to generate a constructor for all final fields.
public class MessageService {

    private final MessageRepository messageRepository; // Repository for database operations on messages.
    private final ChatRepository chatRepository; // Repository for database operations on chats.
    private final MessageMapper mapper; // Utility to map Message entities to DTOs.
    private final FileService fileService; // Service for handling file-related operations (e.g., saving and reading files).
    private final NotificationService notificationService; // Service for sending notifications to users.

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

        // Create a notification for the recipient about the new message.
        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .messageType(messageRequest.getType())
                .content(messageRequest.getContent())
                .senderId(messageRequest.getSenderId())
                .recipientId(messageRequest.getRecipientId())
                .type(NotificationType.MESSAGE)
                .chatName(chat.getChatName(message.getSenderId()))
                .build();

        // Send the notification to the recipient.
        notificationService.sendNotification(message.getRecipientId(), notification);
    }

    /**
     * Retrieves all messages in a chat by chat ID.
     *
     * @param chatId The ID of the chat whose messages are to be retrieved.
     * @return A list of MessageResponse DTOs containing the messages.
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

        // Determine the recipient ID based on the authenticated user.
        final String recipientId = getRecipientId(chat, authentication);

        // Update the state of messages in the chat to be SEEN.
        messageRepository.setMessagesToSeenByChatId(chatId, MessageState.SEEN);

        // Create a notification for the sender about the messages being seen.
        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .type(NotificationType.SEEN)
                .recipientId(recipientId)
                .senderId(getSenderId(chat, authentication))
                .build();

        // Send the notification to the sender.
        notificationService.sendNotification(recipientId, notification);
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

        // Determine the sender and recipient IDs based on the authenticated user.
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

        // Create a notification for the recipient about the new media message.
        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .type(NotificationType.IMAGE)
                .messageType(MessageType.IMAGE)
                .recipientId(recipientId)
                .senderId(senderId)
                .media(FileUtils.readFileFromLocation(filePath))
                .build();

        // Send the notification to the recipient.
        notificationService.sendNotification(recipientId, notification);
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
