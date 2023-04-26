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

/**
 * This class represents the service layer for providing the business logic related to order.
 */
@Service
public class OrderServiceImpl implements OrderService{

    public static final int ORDERS_PER_PAGE = 10;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    /**
     * Creates a new order based on the customer, shipping address, cart items, payment method, and checkout information provided.
     *
     * @param customer the customer associated with the order
     * @param address the shipping address for the order
     * @param cartItems the list of cart items for the order
     * @param paymentMethod the payment method for the order
     * @param checkoutInfo the checkout information for the order
     * @return the newly created order
     */
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

    /**
     * Retrieves a paginated list of orders for a given customer.
     *
     * @param customer the customer for whom to retrieve the orders
     * @param pageNumber the page number of the results to retrieve
     * @param sortField the field to sort the results by
     * @param sortDir the direction in which to sort the results (ascending or descending)
     * @param keyword the keyword to search for in the orders (optional)
     * @return a page of orders for the given customer
     */
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

    /**
     * Retrieves an order for a given customer by order ID.
     *
     * @param orderId the ID of the order to retrieve
     * @param customer the customer for whom to retrieve the order
     * @return the order with the given ID and belonging to the given customer
     */
    @Override
    public Order getOrder(Long orderId, Customer customer) {
        return orderRepository.findByOrderIdAndCustomer(orderId, customer);
    }

    /**
     * Updates the status of the specified order to indicate that a return has been requested.
     *
     * @param request the order return request containing the order ID and reason for the return
     * @param customer the customer who placed the order
     * @throws OrderNotFoundException if the order with the specified ID and customer is not found
     */
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