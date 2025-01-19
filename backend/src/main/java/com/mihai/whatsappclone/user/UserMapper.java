package com.mihai.whatsappclone.user;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * This service class is responsible for mapping the user attributes from the token
 * to a User object. It processes the data extracted from the token and populates
 * the corresponding fields of the User entity.
 */
@Service
public class UserMapper {

    /**
     * Maps user attributes from the provided token attributes to a User object.
     *
     * @param attributes A map containing user attributes from the token.
     * @return A User object populated with the mapped attributes.
     */
    public User fromTokenAttributes(Map<String, Object> attributes) {

        // Create a new User object to hold the mapped attributes
        User user = new User();

        // Map the "sub" attribute to the User's ID if present
        if (attributes.containsKey("sub")) {
            user.setId(attributes.get("sub").toString());
        }

        // Map the "given_name" attribute to the User's first name if present
        if (attributes.containsKey("given_name")) {
            user.setFirstName(attributes.get("given_name").toString());
        }
        // If "given_name" is not present, fallback to "nickname" for first name
        else if (attributes.containsKey("nickname")) {
            user.setFirstName(attributes.get("nickname").toString());
        }

        // Map the "family_name" attribute to the User's last name if present
        if (attributes.containsKey("family_name")) {
            user.setLastName(attributes.get("family_name").toString());
        }

        // Map the "email" attribute to the User's email if present
        if (attributes.containsKey("email")) {
            user.setEmail(attributes.get("email").toString());
        }

        // Set the last seen time for the User to the current time
        user.setLastSeen(LocalDateTime.now());

        // Return the populated User object
        return user;
    }

    /**
     * Maps a User object to a UserResponse object, which is a simplified response DTO.
     *
     * @param user The User object to be mapped.
     * @return A UserResponse object containing selected attributes of the User.
     */
    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId()) // Set the User's ID
                .firstName(user.getFirstName()) // Set the User's first name
                .lastName(user.getLastName()) // Set the User's last name
                .email(user.getEmail()) // Set the User's email
                .lastSeen(user.getLastSeen()) // Set the last seen time
                .isOnline(user.isOnline()) // Set the online status
                .build(); // Return the built UserResponse object
    }
}
