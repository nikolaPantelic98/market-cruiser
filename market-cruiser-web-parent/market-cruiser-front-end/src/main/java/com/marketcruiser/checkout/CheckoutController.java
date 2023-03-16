package com.marketcruiser.checkout;

import com.marketcruiser.Utility;
import com.marketcruiser.address.AddressServiceImpl;
import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.CartItem;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.ShippingRate;
import com.marketcruiser.customer.CustomerServiceImpl;
import com.marketcruiser.shipping.ShippingRateServiceImpl;
import com.marketcruiser.shoppingcart.ShoppingCartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CheckoutController {

    private final CheckoutServiceImpl checkoutService;
    private final CustomerServiceImpl customerService;
    private final AddressServiceImpl addressService;
    private final ShippingRateServiceImpl shippingRateService;
    private final ShoppingCartServiceImpl shoppingCartService;

    @Autowired
    public CheckoutController(CheckoutServiceImpl checkoutService, CustomerServiceImpl customerService, AddressServiceImpl addressService,
                              ShippingRateServiceImpl shippingRateService, ShoppingCartServiceImpl shoppingCartService) {
        this.checkoutService = checkoutService;
        this.customerService = customerService;
        this.addressService = addressService;
        this.shippingRateService = shippingRateService;
        this.shoppingCartService = shoppingCartService;
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

        model.addAttribute("checkoutInfo", checkoutInfo);
        model.addAttribute("cartItems", cartItems);

        return "checkout/checkout";
    }

    // helper method that gets the authenticated customer from the request
    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);

        return customerService.getCustomerByEmail(email);
    }
}
