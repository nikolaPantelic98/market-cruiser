package com.marketcruiser.admin.user;

/**
 * UserNotFoundException is an exception that is thrown when a requested user is not found.
 */
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);

    }
}
