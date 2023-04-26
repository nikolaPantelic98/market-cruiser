package com.marketcruiser.common.entity.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * This class represents the details associated with a product. Each product can have multiple
 * details, such as size, color, etc. These details are stored in a separate table in the database.
 * The class is annotated with JPA annotations to map the object to a database table. It has a many-to-one
 * relationship with the Product entity, where each product can have multiple product details.
 */
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
