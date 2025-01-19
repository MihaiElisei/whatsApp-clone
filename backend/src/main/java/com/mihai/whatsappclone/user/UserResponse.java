package com.mihai.whatsappclone.user;

import lombok.*;

import java.time.LocalDateTime;

@Getter // Generates getter methods for all fields.
@Setter // Generates setter methods for all fields.
@AllArgsConstructor // Generates a constructor with all fields as parameters.
@NoArgsConstructor // Generates a no-argument constructor.
@Builder // Provides a builder pattern for the class to create instances with ease.
public class UserResponse {

    private String id; // The unique identifier of the user.
    private String firstName; // The first name of the user.
    private String lastName; // The last name of the user.
    private String email; // The email of the user.
    private LocalDateTime lastSeen; // The last time the user was seen (e.g., last login).
    private boolean isOnline; // Indicates whether the user is currently online.

}
