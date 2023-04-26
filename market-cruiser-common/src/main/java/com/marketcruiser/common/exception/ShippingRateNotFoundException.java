package com.marketcruiser.common.exception;

/**
 * ShippingRateNotFoundException is an exception that is thrown when a requested shipping rate is not found.
 */
public class ShippingRateNotFoundException extends Exception{

    public ShippingRateNotFoundException(String message) {
        super(message);
    }
}
