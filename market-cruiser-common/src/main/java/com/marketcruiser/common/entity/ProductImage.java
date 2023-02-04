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
        name = "product_images"
)
public class ProductImage {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long productImageId;
    @Column(
            name = "name",
            nullable = false
    )
    private String name;

    @ManyToOne
    @JoinColumn(
            name = "product_id"
    )
    private Product product;


    public ProductImage(String name, Product product) {
        this.name = name;
        this.product = product;
    }

    @Transient
    public String getImagePath() {
        return "/product-images/" + product.getProductId() + "/extras/" + this.name;
    }
}
