package com.mihai.whatsappclone.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This repository interface extends JpaRepository and provides CRUD operations
 * for the User entity. It includes custom query methods for finding a user by email
 * and by their public ID.
 */
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Custom query method to find a user by their email.
     * It uses the named query defined in UserConstants.
     *
     * @param email The email of the user to be retrieved.
     * @return An Optional containing the found user, or empty if no user is found.
     */
    @Query(name = UserConstants.FIND_USER_BY_EMAIL)
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * Custom query method to find a user by their public ID.
     * It uses the named query defined in UserConstants.
     *
     * @param publicId The public ID of the user to be retrieved.
     * @return An Optional containing the found user, or empty if no user is found.
     */
    @Query(name = UserConstants.FIND_USER_BY_PUBLIC_ID)
    Optional<User> findByPublicId(String publicId);

    /**
     * Custom query method to find all users except the one with the specified public ID.
     * This is useful for excluding the current user when retrieving a list of users.
     *
     * @param senderId The public ID of the current user, who will be excluded from the result.
     * @return A list of users excluding the one with the provided senderId.
     */
    @Query(name = UserConstants.FIND_ALL_USERS_EXCEPT_SELF) // Custom query using a named query
    List<User> findAllUsersExceptSelf(@Param("publicId") String senderId);
}
