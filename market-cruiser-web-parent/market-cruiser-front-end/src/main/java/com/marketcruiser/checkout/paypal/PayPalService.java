package com.marketcruiser.checkout.paypal;

public interface PayPalService {

    boolean validateOrder(String orderId) throws PayPalApiException;
}
