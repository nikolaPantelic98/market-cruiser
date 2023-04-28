package com.marketcruiser.order;

import com.marketcruiser.Utility;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.exception.CustomerNotFoundException;
import com.marketcruiser.common.exception.OrderNotFoundException;
import com.marketcruiser.customer.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * This class defines a Rest Controller for handling order-related requests.
 * It provides methods for initiating the return of an order and for getting the authenticated customer.
 */
@RestController
public class OrderRestController {

    private final OrderServiceImpl orderService;
    private final CustomerServiceImpl customerService;

    @Autowired
    public OrderRestController(OrderServiceImpl orderService, CustomerServiceImpl customerService) {
        this.orderService = orderService;
        this.customerService = customerService;
    }


    /**
     * Handles a request to initiate the return of an order.
     *
     * @param returnRequest - the OrderReturnRequest object containing the order ID, reason and note for the return request
     * @param servletRequest - the HttpServletRequest object representing the request
     * @return a ResponseEntity object with the appropriate status code and message based on the success or failure of the return request
     */
    @PostMapping("/orders/return")
    public ResponseEntity<?> handleOrderReturnRequest(@RequestBody OrderReturnRequest returnRequest, HttpServletRequest servletRequest) {

        System.out.println("Order ID: " + returnRequest.getOrderId());
        System.out.println("Reason: " + returnRequest.getReason());
        System.out.println("Note: " + returnRequest.getNote());
        Customer customer = null;

        try {
            customer = getAuthenticatedCustomer(servletRequest);
        } catch (CustomerNotFoundException exception) {
            return new ResponseEntity<>("Authentication required", HttpStatus.BAD_REQUEST);
        }

        try {
            orderService.setOrderReturnRequested(returnRequest, customer);
        } catch (OrderNotFoundException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()), HttpStatus.OK);
    }

    /**
     * Helper method that gets the authenticated customer from the request.
     *
     * @param request - the HttpServletRequest object representing the request
     * @return a Customer object representing the authenticated customer
     * @throws CustomerNotFoundException if the customer is not found in the request
     */
    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("No authenticated customer");
        }

        return customerService.getCustomerByEmail(email);
    }
}
