package com.marketcruiser.security;

import com.marketcruiser.common.entity.AuthenticationType;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.customer.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class implements a custom success handler for database authentication. It extends the
 * {@link  SavedRequestAwareAuthenticationSuccessHandler} class provided by Spring Security.
 */
@Component
public class DatabaseLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final CustomerServiceImpl customerService;

    @Autowired
    public DatabaseLoginSuccessHandler(@Lazy CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }


    /**
     * This method is called when a user successfully logs in with an authentication method
     * It updates the customer's authentication type to DATABASE.
     *
     * @param request - the HttpServletRequest object
     * @param response - the HttpServletResponse object
     * @param authentication - the Authentication object representing the logged-in user
     * @throws ServletException - a ServletException is thrown when there is an issue with the servlet
     * @throws IOException - an IOException is thrown when there is an issue with I/O
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();
        Customer customer = userDetails.getCustomer();

        customerService.updateAuthenticationType(customer, AuthenticationType.DATABASE);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
