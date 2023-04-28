package com.marketcruiser.checkout.paypal;

/**
 * Custom exception class for handling exceptions thrown by the PayPal API.
 */
public class PayPalApiException extends Exception{

    public PayPalApiException(String message) {
        super(message);
    }
}
