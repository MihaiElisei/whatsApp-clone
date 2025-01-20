package com.mihai.whatsappclone.message;

import com.mihai.whatsappclone.chat.Chat;
import com.mihai.whatsappclone.common.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a message within a chat.
 * This entity extends {@link BaseAuditingEntity}, which provides auditing fields like createdDate and lastModifiedDate.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity // Marks this class as a JPA entity.
@Table(name = "messages") // Maps the entity to the "messages" table in the database.
@NamedQuery( // Predefined JPA query to fetch messages by chat ID.
        name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID,
        query = "SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.createdDate"
)
@NamedQuery( // Predefined JPA query to update the state of messages in a specific chat.
        name = MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT,
        query = "UPDATE Message SET state = :newState WHERE chat.id = :chatId"
)
public class Message extends BaseAuditingEntity {

    /**
     * The unique identifier of the message.
     */
    @Id // Specifies the primary key of the entity.
    @SequenceGenerator( // Configures a sequence generator for ID generation.
            name = "msg_seq",
            sequenceName = "msg_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "msg_seq") // Uses a sequence strategy for ID generation.
    private String id;

    /**
     * The content of the message, stored as a text column in the database.
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * The state of the message (e.g., sent, delivered, read).
     */
    @Enumerated(EnumType.STRING) // Maps the MessageState enum to a string in the database.
    private MessageState state;

    /**
     * The type of the message (e.g., text, image, video).
     */
    @Enumerated(EnumType.STRING) // Maps the MessageType enum to a string in the database.
    private MessageType type;

    /**
     * The chat to which the message belongs.
     */
    @ManyToOne // Defines a many-to-one relationship with the Chat entity.
    @JoinColumn(name = "chat_id") // Specifies the foreign key column for the related chat.
    private Chat chat;

    /**
     * The ID of the user who sent the message.
     * This field is mandatory and cannot be null.
     */
    @Column(name = "sender_id", nullable = false)
    private String senderId;

    /**
     * The ID of the user who received the message.
     * This field is mandatory and cannot be null.
     */
    @Column(name = "recipient_id", nullable = false)
    private String recipientId;

    /**
     * The file path of the media associated with the message, if any.
     */
    private String mediaFilePath;
}
