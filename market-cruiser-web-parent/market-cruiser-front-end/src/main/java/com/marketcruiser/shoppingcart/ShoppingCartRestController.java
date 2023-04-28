package com.marketcruiser.shoppingcart;

import com.marketcruiser.Utility;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.exception.CustomerNotFoundException;
import com.marketcruiser.customer.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * This class represents a REST controller for managing shopping cart related functionality.
 * It provides endpoints for adding, updating and removing products from a customer's cart.
 */
@RestController
public class ShoppingCartRestController {

    private final ShoppingCartServiceImpl shoppingCartService;
    private final CustomerServiceImpl customerService;

    @Autowired
    public ShoppingCartRestController(ShoppingCartServiceImpl shoppingCartService, CustomerServiceImpl customerService) {
        this.shoppingCartService = shoppingCartService;
        this.customerService = customerService;
    }


    /**
     * Adds a specified quantity of a product to a customer's shopping cart
     *
     * @param productId The ID of the product to be added to cart
     * @param quantity The quantity of the product to be added to cart
     * @param request The HttpServletRequest object containing the customer's authentication details
     * @return A message indicating the status of the operation
     */
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

    /**
     * Helper method that gets the authenticated customer from the request
     *
     * @param request The HttpServletRequest object containing the customer's authentication details
     * @return The authenticated customer
     * @throws CustomerNotFoundException Thrown if the customer is not found in the system
     */
    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("No authenticated customer");
        }

        return customerService.getCustomerByEmail(email);
    }

    /**
     * Updates the quantity of a product in the cart for the authenticated customer
     *
     * @param productId The ID of the product to be updated
     * @param quantity The new quantity of the product
     * @param request The HttpServletRequest object containing the customer's authentication details
     * @return The new subtotal of the updated product
     */
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

    /**
     * Removes a product from the cart for the authenticated customer.
     *
     * @param productId the ID of the product to remove from the cart.
     * @param request the HttpServletRequest object containing the request information.
     * @return a String indicating that the product has been successfully removed from the cart.
     * @throws CustomerNotFoundException if the customer is not authenticated.
     */
    @DeleteMapping("/cart/remove/{productId}")
    public String removeProduct(@PathVariable Long productId, HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            shoppingCartService.removeProduct(productId, customer);
            return "The product has been removed from your shopping cart.";

        } catch (CustomerNotFoundException exception) {
            return "You must login to remove the product.";
        }
    }
}
