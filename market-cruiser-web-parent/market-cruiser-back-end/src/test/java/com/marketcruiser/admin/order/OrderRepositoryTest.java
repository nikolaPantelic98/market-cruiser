package com.marketcruiser.admin.order;

import com.marketcruiser.common.entity.*;
import com.marketcruiser.common.entity.order.*;
import com.marketcruiser.common.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TestEntityManager entityManager;


    @Test
    public void testCreateNewOrderWithSingleProduct() {
        Customer customer = entityManager.find(Customer.class, 14L);
        Product product = entityManager.find(Product.class, 43L);

        Order mainOrder = new Order();
        mainOrder.setOrderTime(new Date());
        mainOrder.setCustomer(customer);
        mainOrder.setFirstName(customer.getFirstName());
        mainOrder.setLastName(customer.getLastName());
        mainOrder.setPhoneNumber(customer.getPhoneNumber());
        mainOrder.setAddressLine1(customer.getAddressLine1());
        mainOrder.setAddressLine2(customer.getAddressLine2());
        mainOrder.setCity(customer.getCity());
        mainOrder.setCountry(customer.getCountry().getName());
        mainOrder.setPostCode(customer.getPostCode());
        mainOrder.setState(customer.getState());

        mainOrder.setShippingCost(5);
        mainOrder.setProductCost(product.getCost());
        mainOrder.setTax(0);
        mainOrder.setSubtotal(product.getPrice());
        mainOrder.setTotal(product.getPrice() + 5);

        mainOrder.setPaymentMethod(PaymentMethod.COD);
        mainOrder.setStatus(OrderStatus.NEW);
        mainOrder.setDeliverDate(new Date());
        mainOrder.setDeliverDays(2);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProduct(product);
        orderDetail.setOrder(mainOrder);
        orderDetail.setProductCost(product.getCost());
        orderDetail.setShippingCost(5);
        orderDetail.setQuantity(1);
        orderDetail.setSubtotal(product.getPrice());
        orderDetail.setUnitPrice(product.getPrice());

        mainOrder.getOrderDetails().add(orderDetail);

        Order savedOrder = orderRepository.save(mainOrder);

        assertThat(savedOrder.getOrderId()).isGreaterThan(0);
    }

    @Test
    public void testListOrders() {
        Iterable<Order> orders = orderRepository.findAll();

        assertThat(orders).hasSizeGreaterThan(0);

        orders.forEach(System.out::println);
    }

    @Test
    public void testUpdateOrder() {
        Long orderId = 2L;
        Order order = orderRepository.findById(orderId).get();

        order.setStatus(OrderStatus.SHIPPING);
        order.setPaymentMethod(PaymentMethod.COD);
        order.setOrderTime(new Date());
        order.setDeliverDays(2);

        Order updatedOrder = orderRepository.save(order);

        assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPING);
    }

    @Test
    public void testGetOrder() {
        Long orderId = 2L;
        Order order = orderRepository.findById(orderId).get();

        assertThat(order).isNotNull();
        System.out.println(order);
    }

    @Test
    public void testDeleteOrder() {
        Long orderId = 2L;
        orderRepository.deleteById(orderId);

        Optional<Order> result = orderRepository.findById(orderId);
        assertThat(result).isNotPresent();
    }

    @Test
    public void testUpdateOrderTracks() {
        Long orderId = 18L;

        Order order = orderRepository.findById(orderId).get();

        OrderTrack newTrack = new OrderTrack();
        newTrack.setOrder(order);
        newTrack.setUpdatedTime(new Date());
        newTrack.setStatus(OrderStatus.NEW);
        newTrack.setNotes(OrderStatus.NEW.defaultDescription());

        OrderTrack processingTrack = new OrderTrack();
        processingTrack.setOrder(order);
        processingTrack.setUpdatedTime(new Date());
        processingTrack.setStatus(OrderStatus.PROCESSING);
        processingTrack.setNotes(OrderStatus.PROCESSING.defaultDescription());

        List<OrderTrack> orderTracks = order.getOrderTracks();
        orderTracks.add(newTrack);
        orderTracks.add(processingTrack);

        Order updatedOrder = orderRepository.save(order);

        assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);
    }

    @Test
    public void testFindByOrderTimeBetween() throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date startTime = dateFormatter.parse("01-04-2023 00:00:00");
        Date endTime = dateFormatter.parse("30-04-2023 23:59:59");

        List<Order> listOrders = orderRepository.findByOrderTimeBetween(startTime, endTime);

        assertThat(listOrders.size()).isGreaterThan(0);

        for (Order order : listOrders) {
            System.out.printf("%s | %s | %.2f | %.2f | %.2f \n",
                    order.getOrderId(), order.getOrderTime(), order.getProductCost(), order.getSubtotal(), order.getTotal());
        }
    }
}
