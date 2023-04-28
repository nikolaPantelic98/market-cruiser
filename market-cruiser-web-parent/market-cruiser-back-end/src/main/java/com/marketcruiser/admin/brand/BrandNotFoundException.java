package com.marketcruiser.admin.brand;

/**
 * BrandNotFoundException is an exception that is thrown when a requested brand is not found.
 */
public class BrandNotFoundException extends Exception {

    public BrandNotFoundException(String message) {
        super(message);
    }
}
