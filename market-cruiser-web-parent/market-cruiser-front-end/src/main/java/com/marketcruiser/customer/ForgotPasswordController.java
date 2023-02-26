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

@Controller
public class ForgotPasswordController {

    private final CustomerServiceImpl customerService;
    private final SettingsServiceImpl settingsService;

    @Autowired
    public ForgotPasswordController(CustomerServiceImpl customerService, SettingsServiceImpl settingsService) {
        this.customerService = customerService;
        this.settingsService = settingsService;
    }


    @GetMapping("/forgot-password")
    public String showRequestForm() {
        return "customers/forgot_password_form";
    }

    @PostMapping("/forgot-password")
    public String processRequestForm(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        try {
            String token = customerService.updateRestPasswordToken(email);
            String link = Utility.getSiteURL(request) + "/reset-password?token=" + token;
            sendEmail(link, email);

            model.addAttribute("message", "We have sent a reset password link to the email. Please check.");

        } catch (CustomerNotFoundException exception) {
            model.addAttribute("error", exception.getMessage());
        } catch (UnsupportedEncodingException | MessagingException exception) {
            model.addAttribute("error", "Could not send email.");
        }

        return "customers/forgot_password_form";
    }

    private void sendEmail(String link, String email) throws MessagingException, UnsupportedEncodingException {
        EmailSettingsBag emailSettings = settingsService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);

        String toAddress = email;
        String subject = "Link to reset yoru password";

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

    @GetMapping("/reset-password")
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
}
