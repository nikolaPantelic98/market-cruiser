package com.marketcruiser.shoppingcart;

import com.marketcruiser.common.entity.CartItem;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findCartItemByCustomer(Customer customer);
    CartItem findCartItemByCustomerAndProduct(Customer customer, Product product);
    @Modifying
    @Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.customer.customerId = ?2 AND c.product.productId = ?3")
    void updateQuantity(Integer quantity, Long customerId, Long productId);
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.customer.customerId = ?1 AND c.product.productId = ?2")
    void deleteCartItemByCustomerAndProduct(Long customerId, Long productId);
}
