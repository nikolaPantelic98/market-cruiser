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
        name = "categories"
)
public class Category {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long categoryId;
    @Column(
            name = "name",
            length = 128,
            nullable = false,
            unique = true
    )
    private String name;
    @Column(
            name = "alias",
            length = 64,
            nullable = false,
            unique = true
    )
    private String alias;
    @Column(
            name = "image",
            length = 128,
            nullable = false
    )
    private String image;
    @Column(
            name = "enabled"
    )
    private boolean enabled;

    @OneToOne
    @JoinColumn(
            name = "parent_id"
    )
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private Set<Category> children = new HashSet<>();
}
