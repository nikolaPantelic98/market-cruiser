package com.marketcruiser.common.exception;

/**
 * CategoryNotFoundException is an exception that is thrown when a requested category is not found.
 */
public class CategoryNotFoundException extends Exception {

    public CategoryNotFoundException(String message) {
        super(message);
    }
}
