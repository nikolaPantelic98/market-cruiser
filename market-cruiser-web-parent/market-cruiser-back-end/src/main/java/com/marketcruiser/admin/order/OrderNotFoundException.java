package com.marketcruiser.admin.order;

/**
 * OrderNotFoundException is an exception that is thrown when a requested order is not found.
 */
public class OrderNotFoundException extends Exception{

    public OrderNotFoundException(String message) {
        super(message);
    }
}
