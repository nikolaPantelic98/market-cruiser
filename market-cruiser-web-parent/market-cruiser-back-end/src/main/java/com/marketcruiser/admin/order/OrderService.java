package com.marketcruiser.admin.order;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.order.Order;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {

    Page<Order> listOrdersByPage(int pageNumber, String sortField, String sortDir, String keyword);
    Order getOrder(Long orderId) throws OrderNotFoundException;
    void deleteOrder(Long orderId) throws OrderNotFoundException;
    List<Country> listAllCountries();
}
