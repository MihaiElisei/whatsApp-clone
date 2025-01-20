package com.mihai.whatsappclone.user;

/**
 * This class holds constant string values for the queries related to user operations.
 * These constants are used to reference query names, ensuring consistency across the application.
 */
public class UserConstants {

    /**
     * Constant for the query name to find a user by their email.
     * This query is used when looking up a user based on their email address.
     */
    public static final String FIND_USER_BY_EMAIL = "Users.findUserByEmail";

    /**
     * Constant for the query name to find all users except the current user.
     * This query is used when retrieving a list of users excluding the user who is performing the operation.
     */
    public static final String FIND_ALL_USERS_EXCEPT_SELF = "Users.findAllUsersExceptSelf";

    /**
     * Constant for the query name to find a user by their public ID.
     * This query is used when retrieving a user based on their public identifier.
     */
    public static final String FIND_USER_BY_PUBLIC_ID = "Users.findUserByPublicId";

    /**
     * Private constructor to prevent instantiation of this utility class.
     * Constants should not be instantiated or modified.
     */
    private UserConstants() {}
}
