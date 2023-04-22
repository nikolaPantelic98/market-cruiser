package com.marketcruiser.order;

import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN o.orderDetails od JOIN od.product p "
        + "WHERE o.customer.customerId = ?2 "
        + "AND (p.name LIKE %?1% OR o.status LIKE %?1%)")
    Page<Order> findAll(String keyword, Long customerId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.customer.customerId = ?1")
    Page<Order> findAll (Long customerId, Pageable pageable);

    Order findByOrderIdAndCustomer(Long orderId, Customer customer);
}
