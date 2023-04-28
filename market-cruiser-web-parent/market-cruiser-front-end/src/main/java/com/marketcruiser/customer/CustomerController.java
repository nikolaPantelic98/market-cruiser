package com.marketcruiser.customer;

import com.marketcruiser.Utility;
import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.security.CustomerUserDetails;
import com.marketcruiser.security.oauth.CustomerOAuth2User;
import com.marketcruiser.settings.EmailSettingsBag;
import com.marketcruiser.settings.SettingsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * The CustomerController class is a Spring MVC Controller that handles HTTP requests related to managing a customer
 */
@Controller
@Transactional
public class CustomerController {

    private final CustomerServiceImpl customerService;
    private final SettingsServiceImpl settingsService;

    @Autowired
    public CustomerController(CustomerServiceImpl customerService, SettingsServiceImpl settingsService) {
        this.customerService = customerService;
        this.settingsService = settingsService;
    }


    /**
     * This method displays the registration form.
     *
     * @param model the model object containing the list of countries and the customer object
     * @return the view name for the registration form
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        List<Country> listCountries = customerService.listAllCountries();

        model.addAttribute("listCountries", listCountries);
        model.addAttribute("pageTitle", "Customer Registration");
        model.addAttribute("customer", new Customer());

        return "register/register_form";
    }

    /**
     * This method creates a new customer in the database.
     *
     * @param customer the customer object to be created
     * @param model the model object containing the page title
     * @param request the HTTP servlet request
     * @return the view name for the registration success page
     * @throws MessagingException if there is an issue with sending the verification email
     * @throws UnsupportedEncodingException if there is an issue with encoding the email content
     */
    @PostMapping("/create_customer")
    public String createCustomer(Customer customer, Model model, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        customerService.registerCustomer(customer);
        sendVerificationEmail(request, customer);

        model.addAttribute("pageTitle", "Registration Succeeded!");

        return "/register/register_success";
    }

    /**
     * This method is a helper method that sends a verification email to a new customer.
     *
     * @param request the HTTP servlet request
     * @param customer the customer object for whom to send the verification email
     * @throws MessagingException if there is an issue with sending the email
     * @throws UnsupportedEncodingException if there is an issue with encoding the email content
     */
    private void sendVerificationEmail(HttpServletRequest request, Customer customer) throws MessagingException, UnsupportedEncodingException {
        EmailSettingsBag emailSettings = settingsService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

        String toAddress = customer.getEmail();
        String subject = emailSettings.getCustomerVerifySubject();
        String content = emailSettings.getCustomerVerifyContent();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", customer.getFullName());

        String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

        System.out.println("to Address: " + toAddress);
        System.out.println("Verify URL: " + verifyURL);
    }

    /**
     * This method is used to verify the customer account with the given verification code.
     *
     * @param code the verification code to verify the customer account
     * @param model the Spring model object used to pass data to the view
     * @return a string representing the name of the view to be displayed
     */
    @GetMapping("/verify")
    public String verifyCustomerAccount(@Param("code") String code, Model model) {
        boolean verified = customerService.verifyCustomer(code);

        return "register/" + (verified ? "verify_success" : "verify_fail");
    }

    /**
     * This method is used to return the account details page for the authenticated customer.
     *
     * @param model the Spring model object used to pass data to the view
     * @param request the HTTP request object
     * @return a string representing the name of the view to be displayed
     */
    @GetMapping("/account_details")
    public String viewAccountDetails(Model model, HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        Customer customer = customerService.getCustomerByEmail(email);
        List<Country> listCountries = customerService.listAllCountries();

        model.addAttribute("customer", customer);
        model.addAttribute("listCountries", listCountries);

        return "customers/account_form";
    }

    /**
     * This method is used to update the account details of the customer.
     *
     * @param model the Spring model object used to pass data to the view
     * @param customer the Customer object representing the updated customer account details
     * @param redirectAttributes the Spring RedirectAttributes object used to add attributes to the redirect URL
     * @param request the HTTP request object
     * @return a string representing the redirect URL to be used after the account details are updated
     */
    @PostMapping("/update_account_details")
    public String updateAccountDetails(Model model, Customer customer, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        customerService.updateCustomer(customer);

        redirectAttributes.addFlashAttribute("message", "Your account details have been updated.");

        updateNameForAuthenticatedCustomer(customer, request);

        String redirectOption = request.getParameter("redirect");
        String redirectURL = "redirect:/account_details";

        if ("address_book".equals(redirectOption)) {
            redirectURL = "redirect:/address_book";
        } else if ("cart".equals(redirectOption)) {
            redirectURL = "redirect:/cart";
        } else if ("checkout".equals(redirectOption)) {
            redirectURL = "redirect:/address_book?redirect=checkout";
        }

        return redirectURL;
    }

    /**
     * This is a helper method used to update the name of the authenticated customer.
     *
     * @param customer the Customer object representing the updated customer account details
     * @param request the HTTP request object
     */
    private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
        Object principal = request.getUserPrincipal();

        if (principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
            CustomerUserDetails userDetails = getCustomerUserDetailsObject(principal);
            Customer authenticatedCustomer = userDetails.getCustomer();
            authenticatedCustomer.setFirstName(customer.getFirstName());
            authenticatedCustomer.setLastName(customer.getLastName());

        } else if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) principal;
            CustomerOAuth2User oAuth2User = (CustomerOAuth2User) oAuth2Token.getPrincipal();
            String fullName = customer.getFirstName() + " " + customer.getLastName();
            oAuth2User.setFullName(fullName);
        }
    }

    /**
     * This is a helper method used to return a CustomerUserDetails object based on the principal object passed in.
     *
     * @param principal the principal object representing the authenticated user
     * @return a CustomerUserDetails object representing the authenticated user details
     */
    private CustomerUserDetails getCustomerUserDetailsObject(Object principal) {
        CustomerUserDetails userDetails = null;
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
            userDetails = (CustomerUserDetails) token.getPrincipal();

        } else if (principal instanceof RememberMeAuthenticationToken) {
            RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
            userDetails = (CustomerUserDetails) token.getPrincipal();
        }

        return userDetails;
    }
}
