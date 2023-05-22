package com.marketcruiser.admin.order;

import com.marketcruiser.common.entity.order.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    @Query("SELECT NEW com.marketcruiser.common.entity.order.OrderDetail(d.product.category.name, d.quantity, " +
            "d.productCost, d.shippingCost, d.subtotal) " +
            "FROM OrderDetail d WHERE d.order.orderTime BETWEEN ?1 AND ?2")
    List<OrderDetail> findWithCategoryAndTimeBetween(Date startTime, Date endTime);

    @Query("SELECT NEW com.marketcruiser.common.entity.order.OrderDetail(d.quantity, d.product.name, " +
            "d.productCost, d.shippingCost, d.subtotal) " +
            "FROM OrderDetail d WHERE d.order.orderTime BETWEEN ?1 AND ?2")
    List<OrderDetail> findWithProductAndTimeBetween(Date startTime, Date endTime);
}
