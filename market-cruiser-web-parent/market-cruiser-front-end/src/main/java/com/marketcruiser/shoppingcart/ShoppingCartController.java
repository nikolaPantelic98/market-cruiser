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

/**
 * This class serves as a controller for the shopping cart-related pages, including viewing the contents of the cart
 * and calculating the estimated total price of all items in the cart. It also includes a helper method that retrieves the
 * authenticated customer from the request.
 */
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


    /**
     * Displays the contents of the shopping cart for the authenticated customer and calculates the estimated total price
     * of all items in the cart.
     *
     * @param model the Model object containing data to be displayed on the page
     * @param request the HttpServletRequest containing information about the request
     * @return the name of the view to be displayed
     */
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

    /**
     * Retrieves the authenticated customer from the request.
     *
     * @param request the HttpServletRequest containing the authenticated customer's email address
     * @return the authenticated customer
     */
    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);

        return customerService.getCustomerByEmail(email);
    }
}
