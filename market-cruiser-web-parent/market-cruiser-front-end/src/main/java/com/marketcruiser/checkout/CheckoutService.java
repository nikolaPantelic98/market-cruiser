package com.marketcruiser.checkout;

import com.marketcruiser.common.entity.CartItem;
import com.marketcruiser.common.entity.ShippingRate;

import java.util.List;

public interface CheckoutService {

    CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate);
}
