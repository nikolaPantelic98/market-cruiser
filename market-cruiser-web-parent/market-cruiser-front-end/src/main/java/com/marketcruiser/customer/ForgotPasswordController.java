package com.marketcruiser.customer;

import com.marketcruiser.Utility;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.exception.CustomerNotFoundException;
import com.marketcruiser.settings.EmailSettingsBag;
import com.marketcruiser.settings.SettingsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

/**
 * The ForgotPasswordController class handles the forgot password functionality
 * for the customer. It allows the user to request a password reset link, reset
 * their password, and receive email notifications.
 */
@Controller
public class ForgotPasswordController {

    private final CustomerServiceImpl customerService;
    private final SettingsServiceImpl settingsService;

    @Autowired
    public ForgotPasswordController(CustomerServiceImpl customerService, SettingsServiceImpl settingsService) {
        this.customerService = customerService;
        this.settingsService = settingsService;
    }


    /**
     * Displays the forgot password form for the user to enter their email.
     *
     * @return the view name for the forgot password form
     */
    @GetMapping("/forgot_password")
    public String showRequestForm() {
        return "customers/forgot_password_form";
    }

    /**
     * Processes the form data submitted by the user when requesting a password reset.
     * Sends an email to the user with a link to reset their password.
     *
     * @param request the HTTP servlet request object
     * @param model the view model object
     * @return the view name for the forgot password form
     */
    @PostMapping("/forgot_password")
    public String processRequestForm(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        try {
            String token = customerService.updateRestPasswordToken(email);
            String link = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(link, email);

            model.addAttribute("message", "We have sent a reset password link to the email. Please check.");

        } catch (CustomerNotFoundException exception) {
            model.addAttribute("error", exception.getMessage());
        } catch (UnsupportedEncodingException | MessagingException exception) {
            model.addAttribute("error", "Could not send email.");
        }

        return "customers/forgot_password_form";
    }

    /**
     * Helper method that is used to email the user with a link to reset their password.
     *
     * @param link the reset password link
     * @param email the user's email address
     * @throws MessagingException if there is an issue with sending the email
     * @throws UnsupportedEncodingException if there is an issue with the email encoding
     */
    private void sendEmail(String link, String email) throws MessagingException, UnsupportedEncodingException {
        EmailSettingsBag emailSettings = settingsService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

        String toAddress = email;
        String subject = "Link to reset your password";

        String content = "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember password, "
                + "or you have not made the request.</p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        helper.setText(content, true);
        mailSender.send(message);
    }

    /**
     * Displays the reset password form for the user to enter their new password.
     *
     * @param token The reset password token.
     * @param model The Model object to add attributes to.
     * @return The view name for the reset password form.
     */
    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param("token") String token, Model model) {
        Customer customer = customerService.getCustomerByResetPasswordToken(token);

        if (customer != null) {
            model.addAttribute("token", token);
        } else {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("message", "Invalid Token");
            return "message";
        }

        return "customers/reset_password_form";
    }

    /**
     * Processes the form data submitted by the user when resetting their password.
     *
     * @param request The HttpServletRequest object to get parameters from.
     * @param model The Model object to add attributes to.
     * @return The view name for the message page.
     */
    @PostMapping("/reset_password")
    public String processResetForm(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        try {
            customerService.updatePassword(token, password);
            model.addAttribute("pageTitle", "Reset Your Password");
            model.addAttribute("title", "Reset Your Password");
            model.addAttribute("message", "You have successfully changed your password.");

            return "message";

        } catch (CustomerNotFoundException exception) {
            model.addAttribute("pageTitle", "Invalid Token");
            model.addAttribute("message", exception.getMessage());

            return "message";
        }
    }
}
