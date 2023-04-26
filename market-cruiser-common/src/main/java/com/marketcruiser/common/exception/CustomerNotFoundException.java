package com.marketcruiser.common.exception;

/**
 * CustomerNotFoundException is an exception that is thrown when a requested customer is not found.
 */
public class CustomerNotFoundException extends Exception{

    public CustomerNotFoundException(String message) {
        super(message);
    }
}
