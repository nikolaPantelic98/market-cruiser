package com.marketcruiser.checkout.paypal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayPalOrderResponse {

    private String id;
    private String status;


    // validates whether the provided order ID matches the internal ID of this order and whether the order status is "COMPLETED"
    public boolean validate(String orderId) {
        return id.equals(orderId) && status.equals("COMPLETED");
    }
}
