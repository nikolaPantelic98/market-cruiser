package com.marketcruiser.admin.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is the REST controller for handling order-related endpoints.
 */
@RestController
public class OrderRestController {

    private final OrderServiceImpl orderService;


    @Autowired
    public OrderRestController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    /**
     * Endpoint for updating the status of an order.
     *
     * @param orderId the ID of the order to update
     * @param status the new status to set for the order
     * @return a Response object containing the ID and new status of the updated order
     */
    @PostMapping("/orders_shipper/update/{orderId}/{status}")
    public Response updateOrderStatus(@PathVariable Long orderId, @PathVariable String status) {
        orderService.updateStatus(orderId, status);

        return new Response(orderId, status);
    }
}

/**
 * This class represents a response object for the updateOrderStatus endpoint.
 */
@Getter
@Setter
@AllArgsConstructor
class Response {
    private Long orderId;
    private String status;
}
