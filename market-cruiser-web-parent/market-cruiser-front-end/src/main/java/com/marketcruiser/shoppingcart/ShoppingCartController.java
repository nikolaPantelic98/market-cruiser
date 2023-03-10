package com.marketcruiser.shoppingcart;

import com.marketcruiser.Utility;
import com.marketcruiser.common.entity.CartItem;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.customer.CustomerServiceImpl;
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

    @Autowired
    public ShoppingCartController(ShoppingCartServiceImpl shoppingCartService, CustomerServiceImpl customerService) {
        this.shoppingCartService = shoppingCartService;
        this.customerService = customerService;
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
