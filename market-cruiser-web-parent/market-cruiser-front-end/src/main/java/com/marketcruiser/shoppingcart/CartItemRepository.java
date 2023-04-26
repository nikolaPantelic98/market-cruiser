package com.marketcruiser.shoppingcart;

import com.marketcruiser.common.entity.CartItem;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * The CartItemRepository interface defines the methods to interact with the {@link CartItem} entity in the database.
 */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Finds all cart items of a specific customer.
     * @param customer the customer to search for
     * @return a list of CartItem objects
     */
    List<CartItem> findCartItemByCustomer(Customer customer);

    /**
     * Finds a specific cart item of a specific customer.
     * @param customer the customer to search for
     * @param product the product to search for
     * @return the CartItem object matching the customer and product, or null if not found
     */
    CartItem findCartItemByCustomerAndProduct(Customer customer, Product product);

    /**
     * Updates the quantity of a specific cart item of a specific customer.
     * @param quantity the new quantity
     * @param customerId the ID of the customer
     * @param productId the ID of the product
     */
    @Modifying
    @Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.customer.customerId = ?2 AND c.product.productId = ?3")
    void updateQuantity(Integer quantity, Long customerId, Long productId);

    /**
     * Deletes a specific cart item of a specific customer.
     * @param customerId the ID of the customer
     * @param productId the ID of the product
     */
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.customer.customerId = ?1 AND c.product.productId = ?2")
    void deleteCartItemByCustomerAndProduct(Long customerId, Long productId);

    /**
     * Deletes all cart items of a specific customer.
     * @param customerId the ID of the customer
     */
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.customer.customerId = ?1")
    void deleteCartItemByCustomer(Long customerId);
}
