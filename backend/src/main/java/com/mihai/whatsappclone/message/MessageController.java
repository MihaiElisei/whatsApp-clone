package com.mihai.whatsappclone.message;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for handling message-related operations.
 * Provides endpoints for creating, updating, and retrieving messages.
 */
@RestController
@RequestMapping("/api/v1/messages") // Base path for all message-related API endpoints.
@RequiredArgsConstructor // Automatically generates a constructor for required fields (final fields).
@Tag(name="Message", description="Endpoints for managing messages.")
public class MessageController {

    private final MessageService messageService;

    /**
     * Endpoint for saving a new message.
     *
     * @param messageRequest The request body containing message details.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Returns 201 Created on success.
    public void saveMessage(@RequestBody MessageRequest messageRequest) {
        messageService.saveMessage(messageRequest);
    }

    /**
     * Endpoint for uploading a media file as part of a chat message.
     *
     * @param chatId        The ID of the chat to which the media belongs.
     * @param file          The media file being uploaded.
     * @param authentication The authentication object for retrieving the current user.
     */
    @PostMapping(value = "/upload-media", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED) // Returns 201 Created on success.
    public void uploadMedia(
            @RequestParam("chat-id") String chatId,
            @Parameter()
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        messageService.uploadMediaMessage(chatId, file, authentication);
    }

    /**
     * Endpoint for marking all messages in a chat as seen.
     *
     * @param chatId        The ID of the chat whose messages will be marked as seen.
     * @param authentication The authentication object for retrieving the current user.
     */
    @PatchMapping
    @ResponseStatus(HttpStatus.ACCEPTED) // Returns 202 Accepted on success.
    public void setMessagesToSeen(@RequestParam("chat-id") String chatId, Authentication authentication) {
        messageService.setMessagesToSeen(chatId, authentication);
    }

    /**
     * Endpoint for retrieving all messages in a specific chat.
     *
     * @param chatId The ID of the chat whose messages are being retrieved.
     * @return A list of messages in the specified chat wrapped in a ResponseEntity.
     */
    @GetMapping("/chat/{chat-id}")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable("chat-id") String chatId
    ) {
        return ResponseEntity.ok(messageService.findChatMessages(chatId));
    }
}
