package com.marketcruiser.shoppingcart;

import com.marketcruiser.common.entity.CartItem;
import com.marketcruiser.common.entity.Customer;

import java.util.List;

public interface ShoppingCartService {

    Integer addProduct(Long productId, Integer quantity, Customer customer) throws ShoppingCartException;
    List<CartItem> listCartItems(Customer customer);
    float updateQuantity(Long productId, Integer quantity, Customer customer);
    void removeProduct(Long productId, Customer customer);
}
