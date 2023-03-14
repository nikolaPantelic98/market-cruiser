package com.marketcruiser.shoppingcart;

import com.marketcruiser.common.entity.CartItem;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.product.Product;
import com.marketcruiser.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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

    @Override
    public List<CartItem> listCartItems(Customer customer) {
        return cartItemRepository.findCartItemByCustomer(customer);
    }

    // Updates the quantity of a product in the cart for a given customer
    @Override
    public float updateQuantity(Long productId, Integer quantity, Customer customer) {
        cartItemRepository.updateQuantity(quantity, customer.getCustomerId(), productId);
        Product product = productRepository.findById(productId).get();
        float subtotal = product.getDiscountPrice() * quantity;

        return subtotal;
    }

    // removes a product from the cart for the given customer
    @Override
    public void removeProduct(Long productId, Customer customer) {
        cartItemRepository.deleteCartItemByCustomerAndProduct(customer.getCustomerId(), productId);
    }
}
