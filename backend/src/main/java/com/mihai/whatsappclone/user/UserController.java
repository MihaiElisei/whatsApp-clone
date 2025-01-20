package com.mihai.whatsappclone.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This controller handles HTTP requests related to users.
 * It exposes endpoints to interact with the user data, such as retrieving all users.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name="User", description="Endpoints for managing users.")
public class UserController {

    // Injecting the UserService to handle the business logic
    private final UserService userService;

    /**
     * Endpoint to fetch all users except the authenticated user.
     *
     * @param authentication The current authenticated user.
     * @return A list of UserResponse objects, representing all users except the authenticated one.
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(Authentication authentication) {
        // Call the service to fetch users and return the list as the response
        return ResponseEntity.ok(userService.getAllUsersExceptSelf(authentication));
    }
}
