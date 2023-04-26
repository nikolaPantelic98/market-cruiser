package com.marketcruiser.order;

import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * The OrderRepository interface defines the methods to interact with the {@link Order} entity in the database.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find all orders associated with a customer and match a keyword.
     * @param keyword The keyword to search for in order details and product names
     * @param customerId The ID of the customer
     * @param pageable The pageable object
     * @return A page of orders matching the given criteria
     */
    @Query("SELECT o FROM Order o JOIN o.orderDetails od JOIN od.product p "
        + "WHERE o.customer.customerId = ?2 "
        + "AND (p.name LIKE %?1% OR o.status LIKE %?1%)")
    Page<Order> findAll(String keyword, Long customerId, Pageable pageable);

    /**
     * Find all orders associated with a customer.
     * @param customerId The ID of the customer
     * @param pageable The pageable object
     * @return A page of orders matching the given criteria
     */
    @Query("SELECT o FROM Order o WHERE o.customer.customerId = ?1")
    Page<Order> findAll (Long customerId, Pageable pageable);

    /**
     * Find an order by its ID and associated customer.
     * @param orderId The ID of the order
     * @param customer The customer associated with the order
     * @return The order with the given ID and customer, or null if not found
     */
    Order findByOrderIdAndCustomer(Long orderId, Customer customer);
}
