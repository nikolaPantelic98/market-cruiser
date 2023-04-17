package com.marketcruiser.admin.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderRestController {

    private final OrderServiceImpl orderService;


    @Autowired
    public OrderRestController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    // endpoint for updating the status of an order
    @PostMapping("/orders_shipper/update/{orderId}/{status}")
    public Response updateOrderStatus(@PathVariable Long orderId, @PathVariable String status) {
        orderService.updateStatus(orderId, status);

        return new Response(orderId, status);
    }
}

@Getter
@Setter
@AllArgsConstructor
class Response {
    private Long orderId;
    private String status;


}
