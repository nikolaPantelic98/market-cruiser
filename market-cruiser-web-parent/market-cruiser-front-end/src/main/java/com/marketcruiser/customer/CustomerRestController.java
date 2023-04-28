package com.marketcruiser.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Controller for handling customer related requests via REST API.
 */
@RestController
public class CustomerRestController {

    private final CustomerServiceImpl customerService;

    @Autowired
    public CustomerRestController(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }


    /**
     * Checks whether the provided email is unique or already exists in the system.
     *
     * @param email Email address to be checked.
     * @return Returns a string "OK" if the email is unique, otherwise returns "Duplicated".
     */
    @PostMapping("/customers/check_unique_email")
    public String checkDuplicateEmail(@Param("email") String email) {
        return customerService.isEmailUnique(email) ? "OK" : "Duplicated";
    }
}
