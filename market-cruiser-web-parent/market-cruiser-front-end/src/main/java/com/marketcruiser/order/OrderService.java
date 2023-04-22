package com.marketcruiser.order;

import com.marketcruiser.checkout.CheckoutInfo;
import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.CartItem;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.order.Order;
import com.marketcruiser.common.entity.order.PaymentMethod;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    Order createOrder(Customer customer, Address address, List<CartItem> cartItems, PaymentMethod paymentMethod,
                      CheckoutInfo checkoutInfo);
    Page<Order> listForCustomerByPage(Customer customer, int pageNumber, String sortField, String sortDir, String keyword);
    Order getOrder(Long orderId, Customer customer);
}
