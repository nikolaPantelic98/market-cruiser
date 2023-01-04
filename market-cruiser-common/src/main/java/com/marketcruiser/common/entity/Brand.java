package com.marketcruiser.common.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(
        name = "brands"
)
public class Brand {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long brandId;
    @Column(
            name = "name",
            nullable = false,
            length = 45,
            unique = true
    )
    private String name;
    @Column(
            name = "logo",
            nullable = false,
            length = 128
    )
    private String logo;

    @ManyToMany
    @JoinTable(
            name = "brands_categories",
            joinColumns = @JoinColumn(
                    name = "brand_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "category_id"
            )
    )
    @ToString.Exclude
    private Set<Category> categories = new HashSet<>();
}
