package com.marketcruiser.order;

import com.marketcruiser.checkout.CheckoutInfo;
import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.CartItem;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.order.Order;
import com.marketcruiser.common.entity.order.PaymentMethod;

import java.util.List;

public interface OrderService {

    Order createOrder(Customer customer, Address address, List<CartItem> cartItems, PaymentMethod paymentMethod,
                      CheckoutInfo checkoutInfo);
}
