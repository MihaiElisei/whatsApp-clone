package com.mihai.whatsappclone.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base class for entities that require auditing fields like createdDate and lastModifiedDate.
 * This class is annotated as a @MappedSuperclass to share its fields with subclasses.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass // Indicates this class is a base class for other entities.
@EntityListeners(AuditingEntityListener.class) // Enables auditing for this entity.
public class BaseAuditingEntity {

    /**
     * Timestamp for when the entity was created.
     * This field is automatically populated and cannot be updated.
     */
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /**
     * Timestamp for when the entity was last modified.
     * This field is automatically updated when the entity is modified.
     */
    @LastModifiedDate
    @Column(name = "last_modified_date", insertable = false)
    private LocalDateTime lastModifiedDate;
}
