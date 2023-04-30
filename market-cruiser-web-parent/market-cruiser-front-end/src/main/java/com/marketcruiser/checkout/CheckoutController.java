package com.marketcruiser.checkout;

import com.marketcruiser.Utility;
import com.marketcruiser.address.AddressServiceImpl;
import com.marketcruiser.checkout.paypal.PayPalApiException;
import com.marketcruiser.checkout.paypal.PayPalServiceImpl;
import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.CartItem;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.ShippingRate;
import com.marketcruiser.common.entity.order.Order;
import com.marketcruiser.common.entity.order.PaymentMethod;
import com.marketcruiser.customer.CustomerServiceImpl;
import com.marketcruiser.order.OrderServiceImpl;
import com.marketcruiser.settings.CurrencySettingsBag;
import com.marketcruiser.settings.EmailSettingsBag;
import com.marketcruiser.settings.PaymentSettingsBag;
import com.marketcruiser.settings.SettingsServiceImpl;
import com.marketcruiser.shipping.ShippingRateServiceImpl;
import com.marketcruiser.shoppingcart.ShoppingCartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * The CheckoutController class is a Spring MVC Controller that handles HTTP requests related to managing a customer's
 * checkout.
 */
@Controller
public class CheckoutController {

    private final CheckoutServiceImpl checkoutService;
    private final CustomerServiceImpl customerService;
    private final AddressServiceImpl addressService;
    private final ShippingRateServiceImpl shippingRateService;
    private final ShoppingCartServiceImpl shoppingCartService;
    private final OrderServiceImpl orderService;
    private final SettingsServiceImpl settingsService;
    private final PayPalServiceImpl payPalService;

    @Autowired
    public CheckoutController(CheckoutServiceImpl checkoutService, CustomerServiceImpl customerService,
                              AddressServiceImpl addressService, ShippingRateServiceImpl shippingRateService,
                              ShoppingCartServiceImpl shoppingCartService, OrderServiceImpl orderService,
                              SettingsServiceImpl settingsService, PayPalServiceImpl payPalService) {
        this.checkoutService = checkoutService;
        this.customerService = customerService;
        this.addressService = addressService;
        this.shippingRateService = shippingRateService;
        this.shoppingCartService = shoppingCartService;
        this.orderService = orderService;
        this.settingsService = settingsService;
        this.payPalService = payPalService;
    }


    /**
     * Displays the checkout page for the authenticated customer.
     *
     * @param model   an instance of the Model class
     * @param request an instance of the HttpServletRequest class
     * @return the view name of the checkout page
     */
    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;

        if (defaultAddress != null) {
            model.addAttribute("shippingAddressName", defaultAddress.getName());
            model.addAttribute("shippingAddressAddress", defaultAddress.getAddress());
            model.addAttribute("shippingAddressPostCodeAndPhoneNumber", defaultAddress.getPostCodeAndPhoneNumber());
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            model.addAttribute("shippingAddressName", customer.getName());
            model.addAttribute("shippingAddressAddress", customer.getAddress());
            model.addAttribute("shippingAddressPostCodeAndPhoneNumber", customer.getPostCodeAndPhoneNumber());
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        if (shippingRate == null) {
            return "redirect:/cart";
        }

        List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

        String currencyCode = settingsService.getCurrencyCode();
        PaymentSettingsBag paymentSettings = settingsService.getPaymentSettings();
        String paypalClientId = paymentSettings.getClientId();

        model.addAttribute("paypalClientId", paypalClientId);
        model.addAttribute("currencyCode", currencyCode);
        model.addAttribute("customer", customer);
        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("cartItems", cartItems);

        return "checkout/checkout";
    }

    /**
     * This method is a helper method that retrieves the authenticated customer from the given HttpServletRequest object.
     *
     * @param request the HttpServletRequest object that will be used to retrieve the authenticated customer's email address
     * @return the Customer object representing the authenticated customer
     */
    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);

        return customerService.getCustomerByEmail(email);
    }

    /**
     * Endpoint for placing an order. Retrieves the payment method, customer information, default address, shipping rate,
     * cart items, and checkout information. Creates an order and sends a confirmation email to the customer. Returns the view
     * "checkout/order_completed".
     *
     * @param request the HttpServletRequest containing the payment method information
     * @return the String representation of the view "checkout/order_completed"
     * @throws MessagingException if there is an issue sending the confirmation email
     * @throws UnsupportedEncodingException if there is an issue encoding the confirmation email content
     */
    @PostMapping("/place_order")
    public String placeOrder(HttpServletRequest request, RedirectAttributes redirectAttributes) throws MessagingException, UnsupportedEncodingException {
        String paymentType = request.getParameter("paymentMethod");
        PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);

        Customer customer = getAuthenticatedCustomer(request);

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;

        if (defaultAddress != null) {
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }

        List<CartItem> cartItems = shoppingCartService.listCartItems(customer);
        CheckoutInfo checkoutInfo = checkoutService.prepareCheckout(cartItems, shippingRate);

        Order createdOrder = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
        shoppingCartService.deleteProductByCustomer(customer);
        sendOrderConfirmationEmail(request, createdOrder);

        redirectAttributes.addFlashAttribute("orderId", createdOrder.getOrderId());

        return "redirect:/checkout/order_completed";
    }


    /**
     * This method handles the GET request for the order completed page.
     * It retrieves the orderId from the flash attribute and passes it to the view.
     * The view for the order completed page is then returned.
     * This method is Post/Redirect/Get (PRG) pattern for handling the confirmation of the order.
     *
     * @param model a Model object which is used to add attributes to the model for use in the view
     * @return the view name for the order completed page
     */
    @GetMapping("/checkout/order_completed")
    public String showOrderCompletedPage(Model model) {
        Long orderId = (Long) model.getAttribute("orderId");

        model.addAttribute("orderId", orderId);

        return "checkout/order_completed";
    }

    /**
     * Sends an email to the customer to confirm that their order has been successfully placed.
     *
     * @param request the HttpServletRequest containing the email settings
     * @param order the Order object to send the confirmation for
     * @throws MessagingException if there is an issue sending the confirmation email
     * @throws UnsupportedEncodingException if there is an issue encoding the confirmation email content
     */
    private void sendOrderConfirmationEmail(HttpServletRequest request, Order order) throws MessagingException, UnsupportedEncodingException {
        EmailSettingsBag emailSettings = settingsService.getEmailSettings();
        JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
        mailSender.setDefaultEncoding("utf-8");

        String toAddress = order.getCustomer().getEmail();
        String subject = emailSettings.getOrderConfirmationSubject();
        String content = emailSettings.getOrderConfirmationContent();

        subject = subject.replace("[[orderId]]", String.valueOf(order.getOrderId()));

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
        helper.setTo(toAddress);
        helper.setSubject(subject);

        DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
        String orderTime = dateFormatter.format(order.getOrderTime());

        CurrencySettingsBag currencySettings = settingsService.getCurrencySettings();
        String totalAmount = Utility.formatCurrency(order.getTotal(), currencySettings);

        String verifyURL = Utility.getSiteURL(request) + "/orders/page/1?sortField=orderTime&sortDir=desc";

        content = content.replace("[[name]]", order.getCustomer().getFullName());
        content = content.replace("[[orderId]]", String.valueOf(order.getOrderId()));
        content = content.replace("[[orderTime]]", orderTime);
        content = content.replace("[[name]]", order.getCustomerName());
        content = content.replace("[[address]]", order.getCustomerAddress());
        content = content.replace("[[postCodeAndPhoneNumber]]", order.getCustomerPostCodeAndPhoneNumber());
        content = content.replace("[[total]]", totalAmount);
        content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());
        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);
        mailSender.send(message);
    }

    /**
     * Handles the processing of a PayPal order. Validates the order information using the PayPal API,
     * and calls the {@link #placeOrder(HttpServletRequest)} method to complete the checkout process
     * if the order information is valid. If the validation fails, a "Checkout Failure" message is
     * displayed to the user.
     *
     * @param request the HTTP request object containing the order ID parameter
     * @param model the model object to which the page title and message are added
     * @return a string indicating the name of the view to be displayed to the user
     * @throws UnsupportedEncodingException if there is a problem with the character encoding
     * @throws MessagingException if there is a problem with sending an email
     */
    @PostMapping("/process_paypal_order")
    public String processPayPalOrder(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException, MessagingException {
        String orderId = request.getParameter("orderId");
        String pageTitle = "Checkout Failure";
        String message = null;

        try {
            if (payPalService.validateOrder(orderId)) {
                return placeOrder(request, redirectAttributes);
            } else {
                pageTitle = "Checkout Failure";
                message = "ERROR: Transaction could not be completed because order information is invalid";
            }
        } catch (PayPalApiException exception) {
            message = "ERROR: Transaction failed due to error: " + exception.getMessage();
        }

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("message", message);

        return "message";
    }
}
