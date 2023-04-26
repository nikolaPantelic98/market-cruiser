package com.marketcruiser.common.exception;

/**
 * ProductNotFoundException is an exception that is thrown when a requested product is not found.
 */
public class ProductNotFoundException extends Exception {

    public ProductNotFoundException(String message) {
        super(message);
    }
}
