package com.marketcruiser.common.entity.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * This class represents a product image.
 */
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

    /**
     * Get the path of the image file.
     */
    @Transient
    public String getImagePath() {
        return "/product-images/" + product.getProductId() + "/extras/" + this.name;
    }
}
