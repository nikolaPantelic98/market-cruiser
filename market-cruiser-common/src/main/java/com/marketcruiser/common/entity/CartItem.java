package com.marketcruiser.common.entity;

import com.marketcruiser.common.entity.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Represents an item in a customer's shopping cart.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "cart_items"
)
public class CartItem {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long cartItemId;

    @ManyToOne()
    @JoinColumn(
            name = "customer_id"
    )
    private Customer customer;

    @ManyToOne()
    @JoinColumn(
            name = "product_id"
    )
    private Product product;

    private int quantity;

    @Transient
    private float shippingCost;


    @Override
    public String toString() {
        return "CartItem{" +
                "cartItemId=" + cartItemId +
                ", customer=" + customer.getFullName() +
                ", product=" + product.getShortName() +
                ", quantity=" + quantity +
                '}';
    }

    /**
     * Calculates and returns the subtotal for this cart item.
     */
    @Transient
    public float getSubtotal() {
        return product.getDiscountPrice() * quantity;
    }

    /**
     * Returns the shipping cost for this cart item.
     */
    @Transient
    public float getShippingCost() {
        return shippingCost;
    }
}
