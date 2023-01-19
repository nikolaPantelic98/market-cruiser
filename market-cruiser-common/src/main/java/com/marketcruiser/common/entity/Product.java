package com.marketcruiser.common.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(
        name = "products"
)
public class Product {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long productId;
    @Column(
            name = "name",
            unique = true,
            length = 256,
            nullable = false
    )
    private String name;
    @Column(
            name = "alias",
            unique = true,
            length = 256,
            nullable = false
    )
    private String alias;
    @Column(
            name = "short_description",
            length = 512,
            nullable = false
    )
    private String shortDescription;
    @Column(
            name = "full_description",
            length = 4096,
            nullable = false
    )
    private String fullDescription;

    @Column(
            name = "created_time"
    )
    private Date createdTime;
    @Column(
            name = "updated_time"
    )
    private Date updatedTime;

    @Column(
            name = "enabled"
    )
    private boolean enabled;
    @Column(
            name = "in_stock"
    )
    private boolean inStock;

    @Column(
            name = "cost"
    )
    private float cost;
    @Column(
            name = "price"
    )
    private float price;
    @Column(
            name = "discount_percent"
    )
    private float discountPercent;

    @Column(
            name = "length"
    )
    private float length;
    @Column(
            name = "width"
    )
    private float width;
    @Column(
            name = "height"
    )
    private float height;
    @Column(
            name = "weight"
    )
    private float weight;

    @ManyToOne
    @JoinColumn(
            name = "category_id"
    )
    private Category category;

    @ManyToOne
    @JoinColumn(
            name = "brand_id"
    )
    private Brand brand;
}
