package com.marketcruiser.admin.shippingrate;

/**
 * Exception class to be thrown when trying to create a new shipping rate that already exists.
 */
public class ShippingRateAlreadyExistsException extends Exception{

    public ShippingRateAlreadyExistsException(String message) {
        super(message);
    }
}
