package com.marketcruiser.shoppingcart;

import com.marketcruiser.common.entity.CartItem;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService{

    private final CartItemRepository cartItemRepository;

    @Autowired
    public ShoppingCartServiceImpl(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }


    // adds the product with the given quantity to the shopping cart of the given customer
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
}
