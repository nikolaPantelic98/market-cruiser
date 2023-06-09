package com.marketcruiser.common.entity;

import com.marketcruiser.common.entity.product.Product;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(
        name = "reviews"
)
public class Review {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long reviewId;
    @Column(
            name = "headline",
            length = 256,
            nullable = false
    )
    private String headline;
    @Column(
            name = "comment",
            length = 600,
            nullable = false
    )
    private String comment;

    private int rating;
    @Column(
            name = "review_time",
            nullable = false
    )
    private Date reviewTime;

    @ManyToOne
    @JoinColumn(
            name = "product_id"
    )
    private Product product;

    @ManyToOne
    @JoinColumn(
            name = "customer_id"
    )
    private Customer customer;
}
