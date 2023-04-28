package com.marketcruiser.checkout.paypal;

/**
 * Interface for PayPalService to validate an order using PayPal API.
 */
public interface PayPalService {

    /**
     * Validates an order with the given order ID using PayPal API.
     *
     * @param orderId the ID of the order to be validated
     * @return true if the order is valid, false otherwise
     * @throws PayPalApiException if there is an error while validating the order with PayPal API
     */
    boolean validateOrder(String orderId) throws PayPalApiException;
}
