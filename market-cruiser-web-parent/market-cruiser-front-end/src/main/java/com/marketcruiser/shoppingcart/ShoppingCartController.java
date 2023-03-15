package com.marketcruiser.shoppingcart;

import com.marketcruiser.Utility;
import com.marketcruiser.address.AddressServiceImpl;
import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.CartItem;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.ShippingRate;
import com.marketcruiser.customer.CustomerServiceImpl;
import com.marketcruiser.shipping.ShippingRateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ShoppingCartController {

    private final ShoppingCartServiceImpl shoppingCartService;
    private final CustomerServiceImpl customerService;
    private final ShippingRateServiceImpl shippingRateService;
    private final AddressServiceImpl addressService;

    @Autowired
    public ShoppingCartController(ShoppingCartServiceImpl shoppingCartService, CustomerServiceImpl customerService, ShippingRateServiceImpl shippingRateService, AddressServiceImpl addressService) {
        this.shoppingCartService = shoppingCartService;
        this.customerService = customerService;
        this.shippingRateService = shippingRateService;
        this.addressService = addressService;
    }


    // view the contents of the shopping cart for the authenticated customer and calculates the estimated total price of all items in the cart
    @GetMapping("/cart")
    public String viewCart(Model model, HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        List<CartItem> cartItems = shoppingCartService.listCartItems(customer);

        float estimatedTotal = 0.0F;

        for (CartItem item : cartItems) {
            estimatedTotal += item.getSubtotal();
        }

        Address defaultAddress = addressService.getDefaultAddress(customer);
        ShippingRate shippingRate = null;
        boolean usePrimaryAddressAsDefault = false;

        if (defaultAddress != null) {
            shippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        } else {
            usePrimaryAddressAsDefault = true;
            shippingRate = shippingRateService.getShippingRateForCustomer(customer);
        }


        model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        model.addAttribute("shippingSupported", shippingRate != null);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("estimatedTotal", estimatedTotal);

        return "cart/shopping_cart";
    }

    // helper method that gets the authenticated customer from the request
    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);

        return customerService.getCustomerByEmail(email);
    }
}
