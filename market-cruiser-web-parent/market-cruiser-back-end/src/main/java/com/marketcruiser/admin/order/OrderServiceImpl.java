package com.marketcruiser.admin.order;

import com.marketcruiser.admin.settings.country.CountryRepository;
import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.order.Order;
import com.marketcruiser.common.entity.order.OrderStatus;
import com.marketcruiser.common.entity.order.OrderTrack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class implements the {@link  OrderService} interface and defines the business logic for order operations.
 * It contains methods to retrieve and manipulate Order objects from the database.
 */
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

    /**
     * Retrieves a specific page of orders from the database.
     *
     * @param pageNumber - page number to retrieve
     * @param sortField - field to sort by
     * @param sortDir - sort direction (asc/desc)
     * @param keyword - search keyword
     * @return - a page of orders
     */
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

    /**
     * Retrieves an order from the database by its ID.
     *
     * @param orderId - ID of the order to retrieve
     * @return - the order with the specified ID
     * @throws OrderNotFoundException - if the order is not found
     */
    @Override
    public Order getOrder(Long orderId) throws OrderNotFoundException {
        try {
            return orderRepository.findById(orderId).get();
        } catch (NoSuchElementException exception) {
            throw new OrderNotFoundException("Could not find any orders with ID " + orderId);
        }
    }

    /**
     * Deletes an order from the database by its ID.
     *
     * @param orderId - ID of the order to delete
     * @throws OrderNotFoundException - if the order is not found
     */
    @Override
    public void deleteOrder(Long orderId) throws OrderNotFoundException {
        Long count = orderRepository.countByOrderId(orderId);
        if (count == null || count == 0) {
            throw new OrderNotFoundException("Could not find any orders with ID " + orderId);
        }

        orderRepository.deleteById(orderId);
    }

    /**
     * Retrieves a list of all countries sorted by name in ascending order.
     *
     * @return - a list of countries
     */
    @Override
    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    /**
     * Saves an order to the database by updating an existing order record with
     * the data provided in the orderInForm object.
     *
     * @param orderInForm The order object containing the updated data
     */
    @Override
    public void saveOrder(Order orderInForm) {
        Order orderInDB = orderRepository.findById(orderInForm.getOrderId()).get();
        orderInForm.setOrderTime(orderInDB.getOrderTime());
        orderInForm.setCustomer(orderInDB.getCustomer());

        orderRepository.save(orderInForm);
    }

    /**
     * Updates the status of an order and creates a new order track for the order.
     *
     * @param orderId The ID of the order to update
     * @param status The new status to set for the order
     */
    @Override
    public void updateStatus(Long orderId, String status) {
        Order orderInDB = orderRepository.findById(orderId).get();
        OrderStatus statusToUpdate = OrderStatus.valueOf(status);

        if (!orderInDB.hasStatus(statusToUpdate)) {
            List<OrderTrack> orderTracks = orderInDB.getOrderTracks();

            OrderTrack track = new OrderTrack();
            track.setOrder(orderInDB);
            track.setStatus(statusToUpdate);
            track.setUpdatedTime(new Date());
            track.setNotes(statusToUpdate.defaultDescription());

            orderTracks.add(track);

            orderInDB.setStatus(statusToUpdate);

            orderRepository.save(orderInDB);
        }
    }
}
