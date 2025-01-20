package com.mihai.whatsappclone.chat;

import com.mihai.whatsappclone.common.BaseAuditingEntity;
import com.mihai.whatsappclone.message.MessageState;
import com.mihai.whatsappclone.message.MessageType;
import com.mihai.whatsappclone.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.mihai.whatsappclone.message.Message;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity // Marks this class as a JPA entity, mapped to a database table.
@Table(name = "chat") // Specifies the table name in the database.
@NamedQuery( // Predefined JPA query to find chats by sender ID.
        name = ChatConstants.FIND_CHAT_BY_SENDER_ID,
        query = "SELECT DISTINCT c FROM Chat c WHERE c.sender.id = :senderId OR c.recipient.id = :senderId ORDER BY createdDate DESC"
)
@NamedQuery( // Predefined JPA query to find chats by sender and recipient IDs.
        name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER_ID,
        query = "SELECT DISTINCT c FROM Chat c WHERE (c.sender.id = :senderId AND c.recipient.id = :recipientId) OR (c.sender.id = :recipientId AND c.recipient.id = :senderId)"
)
public class Chat extends BaseAuditingEntity {

    @Id // Specifies the primary key of the entity.
    @GeneratedValue(strategy = GenerationType.UUID) // Automatically generates a unique UUID as the ID.
    private String id;

    @ManyToOne // Defines a many-to-one relationship with the User entity.
    @JoinColumn(name = "sender_id") // Specifies the foreign key column for the sender.
    private User sender;

    @ManyToOne // Defines a many-to-one relationship with the User entity.
    @JoinColumn(name = "recipient_id") // Specifies the foreign key column for the recipient.
    private User recipient;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER) // Defines a one-to-many relationship with the Message entity.
    @OrderBy("createdDate DESC") // Orders the messages by creation date in descending order.
    private List<Message> messages;

    /**
     * Calculates and returns the name of the chat based on the sender's ID.
     * If the sender's ID matches the recipient, the chat name is the sender's name.
     * Otherwise, it is the recipient's name.
     *
     * @param senderId ID of the sender.
     * @return Name of the chat.
     */
    @Transient // Indicates this method is not persisted in the database.
    public String getChatName(final String senderId) {
        if (recipient.getId().equals(senderId)) {
            return sender.getFirstName() + " " + sender.getLastName();
        }
        return recipient.getFirstName() + " " + recipient.getLastName();
    }

    /**
     * Calculates the number of unread messages for the sender.
     * Filters messages where the recipient is the sender and the state is 'SENT'.
     *
     * @param senderId ID of the sender.
     * @return Count of unread messages.
     */
    @Transient
    public long getUnreadMessages(final String senderId) {
        return messages
                .stream()
                .filter(m -> m.getRecipientId().equals(senderId)) // Message is addressed to the sender.
                .filter(m -> MessageState.SENT == m.getState()) // Message was SENT.
                .count();
    }

    /**
     * Retrieves the content of the last message in the chat.
     * If the last message is not text-based, it returns "Attachment".
     *
     * @return Content of the last message or "Attachment" if it's not text-based.
     */
    @Transient
    public String getLastMessage() {
        if (messages != null && !messages.isEmpty()) {
            if (messages.get(0).getType() != MessageType.TEXT) { // Check if the last message is an attachment.
                return "Attachment";
            }
            return messages.getFirst().getContent(); // Return the content of the last message.
        }
        return null; // Return null if there are no messages.
    }

    /**
     * Retrieves the creation date of the last message in the chat.
     *
     * @return The date of the last message or null if there are no messages.
     */
    @Transient
    public LocalDateTime getLastMessageDate() {
        if (messages != null && !messages.isEmpty()) {
            return messages.getFirst().getCreatedDate(); // Return the creation date of the last message.
        }
        return null; // Return null if there are no messages.
    }
}
