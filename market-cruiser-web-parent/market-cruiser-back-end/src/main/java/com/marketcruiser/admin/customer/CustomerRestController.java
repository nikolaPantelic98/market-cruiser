package com.marketcruiser.admin.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {

    private final CustomerServiceImpl customerService;

    @Autowired
    public CustomerRestController(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }


    @PostMapping("/customers/check_email")
    public String checkDuplicateEmail(@Param("customerId") Long customerId, @Param("email") String email) {
        if (customerService.isEmailUnique(customerId, email)) {
            return "OK";
        } else {
            return "Duplicated";
        }
    }

}
