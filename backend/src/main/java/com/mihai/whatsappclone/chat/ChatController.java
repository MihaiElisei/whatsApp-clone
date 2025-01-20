package com.mihai.whatsappclone.chat;

import com.mihai.whatsappclone.common.StringResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing chat-related operations.
 * Provides endpoints for creating a new chat and retrieving chats for a user.
 */
@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Tag(name="Chat", description="Endpoints for managing chats.")
public class ChatController {

    private final ChatService chatService;

    /**
     * Endpoint to create a new chat between a sender and a recipient.
     *
     * @param senderId The ID of the user initiating the chat.
     * @param recipientId The ID of the user who will receive the chat.
     * @return A ResponseEntity containing the ID of the newly created chat.
     */
    @PostMapping
    public ResponseEntity<StringResponse> createChat(
            @RequestParam(name = "sender-id") String senderId,
            @RequestParam(name = "recipient-id") String recipientId
    ) {
        final String chatId = chatService.createChat(senderId, recipientId);
        StringResponse stringResponse = StringResponse.builder()
                .response(chatId) // Encapsulates the chat ID in a StringResponse object.
                .build();

        return ResponseEntity.ok(stringResponse); // Returns the chat ID in the response.
    }

    /**
     * Endpoint to retrieve all chats for the currently authenticated user.
     *
     * @param authentication The authentication object representing the currently logged-in user.
     * @return A ResponseEntity containing a list of ChatResponse objects for the user.
     */
    @GetMapping
    public ResponseEntity<List<ChatResponse>> getChatsByReceiver(Authentication authentication) {
        return ResponseEntity.ok(chatService.getChatsByReceiverId(authentication)); // Retrieves and returns the user's chats.
    }
}
