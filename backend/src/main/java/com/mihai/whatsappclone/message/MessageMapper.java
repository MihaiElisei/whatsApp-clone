package com.mihai.whatsappclone.message;

import com.mihai.whatsappclone.file.FileUtils;
import org.springframework.stereotype.Service;

/**
 * Mapper class for converting Message entities to MessageResponse DTOs.
 * This facilitates the separation of domain models from API response structures.
 */
@Service // Marks this class as a Spring-managed service component.
public class MessageMapper {

    /**
     * Maps a Message entity to a MessageResponse DTO.
     *
     * @param message The Message entity to be mapped.
     * @return A MessageResponse object containing the mapped data.
     */
    public MessageResponse toMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId()) // Maps the ID of the message.
                .content(message.getContent()) // Maps the content of the message.
                .senderId(message.getSenderId()) // Maps the sender's ID.
                .recipientId(message.getRecipientId()) // Maps the recipient's ID.
                .type(message.getType()) // Maps the type of the message (e.g., text, media).
                .state(message.getState()) // Maps the state of the message (e.g., sent, seen).
                .createdAt(message.getCreatedDate()) // Maps the creation timestamp.
                .media(FileUtils.readFileFromLocation(message.getMediaFilePath())) // Reads and maps the media file content associated with the message using `FileUtils`.
                .build(); // Constructs the MessageResponse object.
    }
}
