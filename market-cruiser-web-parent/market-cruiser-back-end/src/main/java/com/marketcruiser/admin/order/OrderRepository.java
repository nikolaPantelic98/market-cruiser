package com.marketcruiser.admin.order;

import com.marketcruiser.common.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * The OrderRepository interface defines the methods to interact with the {@link Order} entity in the database.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find all orders by searching across several fields of the order entity
     *
     * @param keyword  The string to search for
     * @param pageable The pageable object for pagination
     * @return A Page object containing the orders matching the search criteria
     */
    @Query("SELECT o FROM Order o WHERE CONCAT('#', o.orderId) LIKE %?1% OR"
            + " CONCAT(o.firstName, ' ', o.lastName) LIKE %?1% OR"
            + " o.firstName LIKE %?1% OR "
            + " o.lastName LIKE %?1% OR o.phoneNumber LIKE %?1% OR"
            + " o.addressLine1 LIKE %?1% OR o.addressLine2 LIKE %?1% OR"
            + " o.postCode LIKE %?1% OR o.city LIKE %?1% OR"
            + " o.state LIKE %?1% OR o.country LIKE %?1% OR"
            + " o.paymentMethod LIKE %?1% OR o.status LIKE %?1% OR"
            + " o.customer.firstName LIKE %?1% OR"
            + " o.customer.lastName LIKE %?1%")
    Page<Order> findAll(String keyword, Pageable pageable);

    /**
     * Count the number of orders with a specific orderId
     *
     * @param orderId The orderId to count
     * @return The number of orders with the given orderId
     */
    Long countByOrderId(Long orderId);

    @Query("SELECT NEW com.marketcruiser.common.entity.order.Order(o.orderId, o.orderTime, o.productCost, o.subtotal, " +
            "o.total) FROM Order o WHERE o.orderTime BETWEEN ?1 AND ?2 ORDER BY o.orderTime ASC")
    List<Order> findByOrderTimeBetween(Date startTime, Date endTime);
}
