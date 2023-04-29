package com.marketcruiser.admin.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A REST controller for handling customer-related requests.
 */
@RestController
public class CustomerRestController {

    private final CustomerServiceImpl customerService;

    @Autowired
    public CustomerRestController(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }


    /**
     * Handles POST requests to check for duplicate email addresses.
     *
     * @param customerId the customer ID to check (can be null)
     * @param email the email address to check for duplicates
     * @return a string indicating whether the email is unique or duplicated
     */
    @PostMapping("/customers/check_email")
    public String checkDuplicateEmail(@Param("customerId") Long customerId, @Param("email") String email) {
        if (customerService.isEmailUnique(customerId, email)) {
            return "OK";
        } else {
            return "Duplicated";
        }
    }
}