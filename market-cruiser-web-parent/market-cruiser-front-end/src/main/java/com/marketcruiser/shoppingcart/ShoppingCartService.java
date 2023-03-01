package com.marketcruiser.shoppingcart;

import com.marketcruiser.common.entity.Customer;

public interface ShoppingCartService {

    Integer addProduct(Long productId, Integer quantity, Customer customer) throws ShoppingCartException;
}
