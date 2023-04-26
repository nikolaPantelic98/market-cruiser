package com.marketcruiser.shoppingcart;

import com.marketcruiser.common.entity.CartItem;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.product.Product;
import com.marketcruiser.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * This class represents the service layer for providing the business logic related to shopping cart.
 */
@Service
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService{

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ShoppingCartServiceImpl(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }


    /**
     * Adds a product with the given quantity to the shopping cart of the given customer.
     *
     * @param productId the ID of the product to add
     * @param quantity the quantity of the product to add
     * @param customer the customer whose shopping cart is being modified
     * @return the updated quantity of the product in the shopping cart
     * @throws ShoppingCartException if the quantity of the product to add exceeds the maximum allowed quantity of 5
     */
    @Override
    public Integer addProduct(Long productId, Integer quantity, Customer customer) throws ShoppingCartException {
        Integer updateQuantity = quantity;
        Product product = new Product(productId);

        CartItem cartItem = cartItemRepository.findCartItemByCustomerAndProduct(customer, product);

        if (cartItem != null) {
            updateQuantity = cartItem.getQuantity() + quantity;

            if (updateQuantity > 5) {
                throw new ShoppingCartException("Could not add " + quantity + " more item(s) because there's already "
                        + cartItem.getQuantity() + " item(s) in your shopping cart. Maximum allowed quantity is 5.");
            }

        } else {
            cartItem = new CartItem();
            cartItem.setCustomer(customer);
            cartItem.setProduct(product);
        }

        cartItem.setQuantity(updateQuantity);

        cartItemRepository.save(cartItem);

        return updateQuantity;
    }

    /**
     * Returns a list of cart items for the given customer.
     *
     * @param customer the customer whose cart items to retrieve
     * @return a list of cart items
     */
    @Override
    public List<CartItem> listCartItems(Customer customer) {
        return cartItemRepository.findCartItemByCustomer(customer);
    }

    /**
     * Updates the quantity of a product in the shopping cart of the given customer.
     *
     * @param productId the ID of the product whose quantity to update
     * @param quantity the new quantity of the product
     * @param customer the customer whose shopping cart is being modified
     * @return the new subtotal of the product in the shopping cart
     */
    @Override
    public float updateQuantity(Long productId, Integer quantity, Customer customer) {
        cartItemRepository.updateQuantity(quantity, customer.getCustomerId(), productId);
        Product product = productRepository.findById(productId).get();
        float subtotal = product.getDiscountPrice() * quantity;

        return subtotal;
    }

    /**
     * Removes a product from the shopping cart of the given customer.
     *
     * @param productId the ID of the product to remove
     * @param customer the customer whose shopping cart is being modified
     */
    @Override
    public void removeProduct(Long productId, Customer customer) {
        cartItemRepository.deleteCartItemByCustomerAndProduct(customer.getCustomerId(), productId);
    }

    /**
     * Deletes all the cart items in the shopping cart of the given customer.
     * @param customer the customer whose shopping cart will be cleared
     */
    @Override
    public void deleteProductByCustomer(Customer customer) {
        cartItemRepository.deleteCartItemByCustomer(customer.getCustomerId());
    }
}
