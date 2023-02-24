package com.marketcruiser.security.oauth;

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

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final CustomerServiceImpl customerService;

    @Autowired
    public OAuth2LoginSuccessHandler(@Lazy CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        CustomerOAuth2User oAuth2User = (CustomerOAuth2User) authentication.getPrincipal();

        String name = oAuth2User.getName();
        String email = oAuth2User.getEmail();
        String countryCode = request.getLocale().getCountry();

        System.out.println("OAuth2LoginSuccessHandler: " + name + " | " + email);

        Customer customer = customerService.getCustomerByEmail(email);
        if (customer == null) {
            customerService.addNewCustomerUponOAuthLogin(name, email, countryCode);
        } else {
            customerService.updateAuthenticationType(customer, AuthenticationType.GOOGLE);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
