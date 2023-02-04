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
        name = "product_details"
)
public class ProductDetails {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long productDetailsId;
    @Column(
            name = "name",
            nullable = false
    )
    private String name;
    @Column(
            name = "value",
            nullable = false
    )
    private String value;

    @ManyToOne
    @JoinColumn(
            name = "product_id"
    )
    private Product product;


    public ProductDetails(String name, String value, Product product) {
        this.name = name;
        this.value = value;
        this.product = product;
    }
}
