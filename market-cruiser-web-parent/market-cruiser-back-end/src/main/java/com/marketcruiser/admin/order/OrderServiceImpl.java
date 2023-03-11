package com.marketcruiser.admin.order;

import com.marketcruiser.common.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService{

    public static final int ORDERS_PER_PAGE = 10;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // retrieves a specific page of orders from the database
    @Override
    public Page<Order> listOrdersByPage(int pageNumber, String sortField, String sortDir, String keyword) {

        Sort sort = null;

        if ("destination".equals(sortField)) {
            sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
        } else {
            sort = Sort.by(sortField);
        }

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, ORDERS_PER_PAGE, sort);

        if (keyword != null) {
            return orderRepository.findAll(keyword, pageable);
        }

        return orderRepository.findAll(pageable);
    }
}
