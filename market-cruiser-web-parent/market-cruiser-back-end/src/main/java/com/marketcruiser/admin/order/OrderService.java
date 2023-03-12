package com.marketcruiser.admin.order;

import com.marketcruiser.common.entity.Order;
import org.springframework.data.domain.Page;

public interface OrderService {

    Page<Order> listOrdersByPage(int pageNumber, String sortField, String sortDir, String keyword);
    Order getOrder(Long orderId) throws OrderNotFoundException;
}
