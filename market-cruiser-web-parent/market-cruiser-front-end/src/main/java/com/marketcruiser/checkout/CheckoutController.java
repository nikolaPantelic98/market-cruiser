package com.marketcruiser.checkout;

import com.marketcruiser.Utility;
import com.marketcruiser.address.AddressServiceImpl;
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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class CheckoutController {

    private final CheckoutServiceImpl checkoutService;
    private final CustomerServiceImpl customerService;
    private final AddressServiceImpl addressService;
    private final ShippingRateServiceImpl shippingRateService;
    private final ShoppingCartServiceImpl shoppingCartService;
    private final OrderServiceImpl orderService;
    private final SettingsServiceImpl settingsService;

    @Autowired
    public CheckoutController(CheckoutServiceImpl checkoutService, CustomerServiceImpl customerService, AddressServiceImpl addressService,
                              ShippingRateServiceImpl shippingRateService, ShoppingCartServiceImpl shoppingCartService, OrderServiceImpl orderService, SettingsServiceImpl settingsService) {
        this.checkoutService = checkoutService;
        this.customerService = customerService;
        this.addressService = addressService;
        this.shippingRateService = shippingRateService;
        this.shoppingCartService = shoppingCartService;
        this.orderService = orderService;
        this.settingsService = settingsService;
    }


    // displays the checkout page for the authenticated customer
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

    // helper method that gets the authenticated customer from the request
    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);

        return customerService.getCustomerByEmail(email);
    }

    // this method processes a customer's order
    @PostMapping("/place_order")
    public String placeOrder(HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
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

        return "checkout/order_completed";
    }

    // sends an email to the customer to confirm that their order has been successfully placed
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

        content = content.replace("[[name]]", order.getCustomer().getFullName());
        content = content.replace("[[orderId]]", String.valueOf(order.getOrderId()));
        content = content.replace("[[orderTime]]", orderTime);
        content = content.replace("[[name]]", order.getCustomerName());
        content = content.replace("[[address]]", order.getCustomerAddress());
        content = content.replace("[[postCodeAndPhoneNumber]]", order.getCustomerPostCodeAndPhoneNumber());
        content = content.replace("[[total]]", totalAmount);
        content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());

        helper.setText(content, true);
        mailSender.send(message);
    }
}
