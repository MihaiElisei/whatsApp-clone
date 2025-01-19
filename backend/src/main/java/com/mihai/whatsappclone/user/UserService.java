package com.mihai.whatsappclone.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Marks the class as a Spring service.
@RequiredArgsConstructor // Generates a constructor with required arguments (in this case, for final fields).
public class UserService {

    private final UserRepository userRepository; // Repository to access user data from the database.
    private final UserMapper userMapper; // Mapper to convert entities to DTOs (Data Transfer Objects).

    /**
     * Fetches all users except the currently authenticated user.
     *
     * @param connectedUser The authenticated user (the user currently logged in).
     * @return A list of user responses (DTOs) for all users except the authenticated user.
     */
    public List<UserResponse> getAllUsersExceptSelf(Authentication connectedUser){
        // Calls the repository method to get all users except the one matching the authenticated user's name.
        return userRepository.findAllUsersExceptSelf(connectedUser.getName())
                // Maps the result to UserResponse objects using the userMapper.
                .stream()
                .map(userMapper::toUserResponse)
                .toList(); // Collects and returns the result as a List of UserResponse objects.
    }
}
