package com.marketcruiser.admin;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * This class represents the main controller for the MarketCruiser admin application.
 */
@Controller
public class MainController {

    /**
     * Handles a request to view the home page.
     *
     * @return the name of the view for the home page
     */
    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }

    /**
     * Handles a request to view the login page.
     *
     * @return the name of the view for the login page
     */
    @GetMapping("/login")
    public String viewLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }

        return "redirect:/";
    }
}