package com.marketcruiser.admin.order;

import com.marketcruiser.admin.settings.country.CountryRepository;
import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderServiceImpl implements OrderService{

    public static final int ORDERS_PER_PAGE = 10;
    private final OrderRepository orderRepository;
    private final CountryRepository countryRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, CountryRepository countryRepository) {
        this.orderRepository = orderRepository;
        this.countryRepository = countryRepository;
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

    // Retrieves an order from the database by its ID
    @Override
    public Order getOrder(Long orderId) throws OrderNotFoundException {
        try {
            return orderRepository.findById(orderId).get();
        } catch (NoSuchElementException exception) {
            throw new OrderNotFoundException("Could not find any orders with ID " + orderId);
        }
    }

    // deletes order
    @Override
    public void deleteOrder(Long orderId) throws OrderNotFoundException {
        Long count = orderRepository.countByOrderId(orderId);
        if (count == null || count == 0) {
            throw new OrderNotFoundException("Could not find any orders with ID " + orderId);
        }

        orderRepository.deleteById(orderId);
    }

    // returns a list of all countries sorted by name in ascending order
    @Override
    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }
}
