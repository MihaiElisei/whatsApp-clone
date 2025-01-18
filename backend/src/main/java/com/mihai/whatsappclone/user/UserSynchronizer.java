package com.mihai.whatsappclone.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * This service class handles the synchronization of user data with the identity provider (IDP).
 * It retrieves the user information from the IDP using the provided JWT token and updates
 * the user data in the repository.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserSynchronizer {

    /**
     * The repository responsible for interacting with the User data in the database.
     */
    private final UserRepository userRepository;

    /**
     * The service responsible for mapping the token attributes to a User entity.
     */
    private final UserMapper userMapper;

    /**
     * Synchronizes the user data with the identity provider (IDP) based on the provided JWT token.
     *
     * @param token The JWT token containing user data from the IDP.
     */
    public void synchronizeWithIdp(Jwt token) {
        log.info("Synchronizing user with idp");

        // Retrieve the user's email from the token, and proceed if it's available
        getUserEmail(token).ifPresent(email -> {
            log.info("Synchronizing user having email: {}", email);

            // Retrieve the user from the repository by email
            Optional<User> optUser = userRepository.findByEmail(email);

            // Map the token attributes to a User object
            User user = userMapper.fromTokenAttributes(token.getClaims());

            // If the user already exists in the repository, preserve their ID
            optUser.ifPresent(value -> user.setId(optUser.get().getId()));

            // Save or update the user in the repository
            userRepository.save(user);
        });
    }

    /**
     * Retrieves the user's email from the JWT token's claims.
     *
     * @param token The JWT token containing user information.
     * @return An Optional containing the email if present, or empty if not.
     */
    private Optional<String> getUserEmail(Jwt token) {
        Map<String, Object> attributes = token.getClaims();

        // Check if the email attribute is present in the token claims
        if (attributes.containsKey("email")) {
            return Optional.of(attributes.get("email").toString());
        }

        // Return empty if the email is not present
        return Optional.empty();
    }
}
