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
    @ToString.Exclude
    private Set<Category> children = new HashSet<>();


    public Category(String name) {
        this.name = name;
        this.alias = name;
        this.image = "default.png";
    }

    public Category(String name, Category parent) {
        this(name);
        this.parent = parent;
    }

    public Category(Long categoryId) {
        this.categoryId = categoryId;
    }


    public static Category copyCategoryIdAndName(Category category) {
        Category copyCategory = new Category();
        copyCategory.setCategoryId(category.getCategoryId());
        copyCategory.setName(category.getName());

        return copyCategory;
    }

    public static Category copyCategoryIdAndName(Long categoryId, String name) {
        Category copyCategory = new Category();
        copyCategory.setCategoryId(categoryId);
        copyCategory.setName(name);

        return copyCategory;
    }
}
