package com.marketcruiser.checkout.paypal;

import lombok.Getter;
import lombok.Setter;

/**
 * The PayPalOrderResponse class represents a PayPal order response which contains an ID and a status.
 */
@Getter
@Setter
public class PayPalOrderResponse {

    private String id;
    private String status;


    /**
     * Validates whether the provided order ID matches the internal ID of this order and whether the order status is "COMPLETED".
     *
     * @param orderId the ID of the order to validate
     * @return true if the order ID matches and the status is "COMPLETED", false otherwise
     */
    public boolean validate(String orderId) {
        return id.equals(orderId) && status.equals("COMPLETED");
    }
}
