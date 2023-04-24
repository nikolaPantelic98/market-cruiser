package com.marketcruiser.order;

import com.marketcruiser.checkout.CheckoutInfo;
import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.CartItem;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.order.*;
import com.marketcruiser.common.entity.product.Product;
import com.marketcruiser.common.exception.OrderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService{

    public static final int ORDERS_PER_PAGE = 10;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    // creates a new order
    @Override
    public Order createOrder(Customer customer, Address address, List<CartItem> cartItems, PaymentMethod paymentMethod,
                             CheckoutInfo checkoutInfo) {
        Order newOrder = new Order();
        newOrder.setOrderTime(new Date());

        if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
            newOrder.setStatus(OrderStatus.PAID);
        } else {
            newOrder.setStatus(OrderStatus.NEW);
        }

        newOrder.setCustomer(customer);
        newOrder.setProductCost(checkoutInfo.getProductCost());
        newOrder.setSubtotal(checkoutInfo.getProductTotal());
        newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
        newOrder.setTax(0.0F);
        newOrder.setTotal(checkoutInfo.getPaymentTotal());
        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setDeliverDays(checkoutInfo.getDeliverDays());
        newOrder.setDeliverDate(checkoutInfo.getDeliverDate());

        if (address == null) {
            newOrder.copyAddressFromCustomer();
        } else {
            newOrder.copyShippingAddress(address);
        }

        Set<OrderDetail> orderDetails = newOrder.getOrderDetails();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(newOrder);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice(product.getDiscountPrice());
            orderDetail.setProductCost(product.getCost() * cartItem.getQuantity());
            orderDetail.setSubtotal(cartItem.getSubtotal());
            orderDetail.setShippingCost(cartItem.getShippingCost());

            orderDetails.add(orderDetail);
        }

        OrderTrack track = new OrderTrack();
        track.setOrder(newOrder);
        track.setStatus(OrderStatus.NEW);
        track.setNotes(OrderStatus.NEW.defaultDescription());
        track.setUpdatedTime(new Date());

        newOrder.getOrderTracks().add(track);

        return orderRepository.save(newOrder);
    }

    // retrieves a paginated list of orders for a given customer
    @Override
    public Page<Order> listForCustomerByPage(Customer customer, int pageNumber, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, ORDERS_PER_PAGE, sort);

        if (keyword != null) {
            return orderRepository.findAll(keyword, customer.getCustomerId(), pageable);
        }

        return orderRepository.findAll(customer.getCustomerId(), pageable);
    }

    // retrieves an order
    @Override
    public Order getOrder(Long orderId, Customer customer) {
        return orderRepository.findByOrderIdAndCustomer(orderId, customer);
    }

    // Updates the status of the specified order to indicate that a return has been requested
    @Override
    public void setOrderReturnRequested(OrderReturnRequest request, Customer customer) throws OrderNotFoundException {
        Order order = orderRepository.findByOrderIdAndCustomer(request.getOrderId(), customer);

        if (order == null) {
            throw new OrderNotFoundException("Order ID " + request.getOrderId() + " not found.");
        }

        if (order.isReturnRequested()) return;

        OrderTrack track = new OrderTrack();
        track.setOrder(order);
        track.setUpdatedTime(new Date());
        track.setStatus(OrderStatus.RETURN_REQUESTED);

        String notes = "Reason: " + request.getReason();
        if (!"".equals(request.getNote())) {
            notes += ". " + request.getNote();
        }

        track.setNotes(notes);

        order.getOrderTracks().add(track);
        order.setStatus(OrderStatus.RETURN_REQUESTED);

        orderRepository.save(order);
    }


}
