package com.marketcruiser.admin.order;

import com.marketcruiser.common.entity.order.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * The OrderDetailRepository interface defines the methods to interact with the {@link OrderDetail} entity in the database.
 */
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    /**
     * Retrieves a list of OrderDetail objects with the specified category name, within the specified time range.
     *
     * @param startTime the start time of the time range
     * @param endTime   the end time of the time range
     * @return a list of OrderDetail objects matching the criteria
     */
    @Query("SELECT NEW com.marketcruiser.common.entity.order.OrderDetail(d.product.category.name, d.quantity, " +
            "d.productCost, d.shippingCost, d.subtotal) " +
            "FROM OrderDetail d WHERE d.order.orderTime BETWEEN ?1 AND ?2")
    List<OrderDetail> findWithCategoryAndTimeBetween(Date startTime, Date endTime);

    /**
     * Retrieves a list of OrderDetail objects with the specified product name, within the specified time range.
     *
     * @param startTime the start time of the time range
     * @param endTime   the end time of the time range
     * @return a list of OrderDetail objects matching the criteria
     */
    @Query("SELECT NEW com.marketcruiser.common.entity.order.OrderDetail(d.quantity, d.product.name, " +
            "d.productCost, d.shippingCost, d.subtotal) " +
            "FROM OrderDetail d WHERE d.order.orderTime BETWEEN ?1 AND ?2")
    List<OrderDetail> findWithProductAndTimeBetween(Date startTime, Date endTime);
}
