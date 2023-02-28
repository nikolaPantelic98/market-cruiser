package com.marketcruiser.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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


    @Override
    public String toString() {
        return "CartItem{" +
                "cartItemId=" + cartItemId +
                ", customer=" + customer.getFullName() +
                ", product=" + product.getShortName() +
                ", quantity=" + quantity +
                '}';
    }
}
