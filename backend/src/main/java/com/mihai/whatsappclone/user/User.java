package com.mihai.whatsappclone.user;

import com.mihai.whatsappclone.chat.Chat;
import com.mihai.whatsappclone.common.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a user in the application.
 * Extends BaseAuditingEntity to include auditing fields such as createdDate and lastModifiedDate.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity // Marks this class as a JPA entity.
@Table(name = "users") // Maps the entity to the "users" table in the database.
@NamedQuery( // Predefined query to find a user by email.
        name = UserConstants.FIND_USER_BY_EMAIL,
        query = "SELECT u FROM User u WHERE u.email= :email"
)
@NamedQuery( // Predefined query to find all users except the current user.
        name = UserConstants.FIND_ALL_USERS_EXCEPT_SELF,
        query = "SELECT u FROM User u WHERE u.id != :publicId"
)
@NamedQuery( // Predefined query to find a user by their public ID.
        name = UserConstants.FIND_USER_BY_PUBLIC_ID,
        query = "SELECT u FROM User u WHERE u.id = :publicId"
)
public class User extends BaseAuditingEntity {

    private static final int LAST_ACTIVE_INTERVAL = 5; // Interval in minutes to determine if the user is online.

    @Id // Specifies the primary key of the entity.
    private String id;

    private String firstName; // User's first name.

    private String lastName; // User's last name.

    private String email; // User's email address, used for authentication or contact.

    private LocalDateTime lastSeen; // Timestamp indicating the user's last activity.

    // Defines a one-to-many relationship with Chat, where the user is the sender.
    @OneToMany(mappedBy = "sender")
    private List<Chat> chatAsSender;

    // Defines a one-to-many relationship with Chat, where the user is the recipient.
    @OneToMany(mappedBy = "recipient")
    private List<Chat> chatAsRecipient;

    /**
     * Determines if the user is currently online.
     * A user is considered online if their `lastSeen` timestamp is within the last 5 minutes.
     *
     * @return true if the user is online, false otherwise.
     */
    @Transient // Marks this method as non-persistent (not stored in the database).
    public boolean isOnline() {
        return lastSeen != null && lastSeen.isAfter(LocalDateTime.now().minusMinutes(LAST_ACTIVE_INTERVAL));
    }
}
