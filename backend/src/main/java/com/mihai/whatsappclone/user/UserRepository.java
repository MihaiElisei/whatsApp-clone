package com.mihai.whatsappclone.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * This repository interface extends JpaRepository and provides CRUD operations
 * for the User entity. It includes a custom query method to find a user by email.
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
}
