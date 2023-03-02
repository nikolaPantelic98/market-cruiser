package com.marketcruiser.shoppingcart;

import com.marketcruiser.Utility;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.exception.CustomerNotFoundException;
import com.marketcruiser.customer.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ShoppingCartRestController {

    private final ShoppingCartServiceImpl shoppingCartService;
    private final CustomerServiceImpl customerService;

    @Autowired
    public ShoppingCartRestController(ShoppingCartServiceImpl shoppingCartService, CustomerServiceImpl customerService) {
        this.shoppingCartService = shoppingCartService;
        this.customerService = customerService;
    }


    // adds a product with a specified quantity to a customer's shopping cart
    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            Integer updatedQuantity = shoppingCartService.addProduct(productId, quantity, customer);

            return updatedQuantity + " item(s) of this product were added to your shopping cart.";
        } catch (CustomerNotFoundException exception) {
            return "You must login to add this product to cart.";
        } catch (ShoppingCartException exception) {
            return exception.getMessage();
        }
    }

    // helper method that gets the authenticated customer from the request
    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("No authenticated customer");
        }

        return customerService.getCustomerByEmail(email);
    }

    // updates the quantity of a product in the cart for the authenticated customer
    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateQuantity(@PathVariable Long productId, @PathVariable Integer quantity, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            float subtotal = shoppingCartService.updateQuantity(productId, quantity, customer);

            return String.valueOf(subtotal);
        } catch (CustomerNotFoundException exception) {
            return "You must login to change quantity of product.";
        }
    }
}
