package com.marketcruiser;

import com.marketcruiser.security.oauth.CustomerOAuth2User;
import com.marketcruiser.settings.CurrencySettingsBag;
import com.marketcruiser.settings.EmailSettingsBag;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Properties;

/**
 * This class contains utility methods for the MarketCruiser application.
 */
public class Utility {

    /**
     * Returns the site URL by removing the servlet path from the request URL.
     *
     * @param request The HTTP servlet request.
     * @return The site URL.
     */
    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();

        return siteURL.replace(request.getServletPath(), "");
    }

    /**
     * Prepares and returns a JavaMailSenderImpl object using the provided email settings.
     *
     * @param settings The email settings.
     * @return The JavaMailSenderImpl object.
     */
    public static JavaMailSenderImpl prepareMailSender(EmailSettingsBag settings) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(settings.getHost());
        mailSender.setPort(settings.getPort());
        mailSender.setUsername(settings.getUsername());
        mailSender.setPassword(settings.getPassword());

        Properties mailProperties = new Properties();
        mailProperties.setProperty("mail.smtp.auth", settings.getSmtpAuth());
        mailProperties.setProperty("mail.smtp.starttls.enable", settings.getSmtpSecured());

        mailSender.setJavaMailProperties(mailProperties);

        return mailSender;
    }

    /**
     * Retrieves the email of the authenticated customer.
     *
     * @param request The HTTP servlet request.
     * @return The email of the authenticated customer, or null if not authenticated.
     */
    public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
        Object principal = request.getUserPrincipal();
        if (principal == null) return null;

        String customerEmail = null;

        if (principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
            customerEmail = request.getUserPrincipal().getName();
        } else if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) principal;
            CustomerOAuth2User oAuth2User = (CustomerOAuth2User) oAuth2Token.getPrincipal();
            customerEmail = oAuth2User.getEmail();
        }

        return customerEmail;
    }

    /**
     * Formats the specified amount as a currency string using the provided currency settings.
     *
     * @param amount The amount to format.
     * @param currencySettings The currency settings.
     * @return The formatted currency string.
     */
    public static String formatCurrency(float amount, CurrencySettingsBag currencySettings) {
        String symbol = currencySettings.getSymbol();
        String symbolPosition = currencySettings.getSymbolPosition();
        String decimalPointType = currencySettings.getDecimalPointType();
        String thousandPointType = currencySettings.getThousandPointType();
        int decimalDigits = currencySettings.getDecimalDigits();

        String pattern = symbolPosition.equals("Before price") ? symbol : "";
        pattern += "###,###";

        if (decimalDigits > 0) {
            pattern += ".";
            for (int count = 1; count <= decimalDigits; count++) pattern += "#";
        }

        pattern += symbolPosition.equals("After price") ? symbol : "";

        char thousandSeparator = thousandPointType.equals("POINT") ? '.' : ',';
        char decimalSeparator = decimalPointType.equals("POINT") ? '.' : ',';

        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator(decimalSeparator);
        decimalFormatSymbols.setGroupingSeparator(thousandSeparator);

        DecimalFormat formatter = new DecimalFormat(pattern, decimalFormatSymbols);

        return formatter.format(amount);
    }
}