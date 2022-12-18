package com.marketcruiser.admin.user.controller;

import com.marketcruiser.admin.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

    private final UserServiceImpl userService;

    @Autowired
    public UserRestController(UserServiceImpl userService) {
        this.userService = userService;
    }

    // handles a request to check if a given email address is unique among users
    @PostMapping("/users/check-email")
    public String checkDuplicateEmailAddress(@Param("userId") Long userId, @Param("emailAddress") String emailAddress) {
        return userService.isEmailAddressUnique(userId, emailAddress) ? "OK" : "Duplicated";
    }
}
