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

    @Column(
            name = "all_parent_ids",
            length = 256
    )
    private String allParentIDs;

    @OneToOne
    @JoinColumn(
            name = "parent_id"
    )
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @ToString.Exclude
    @OrderBy("name asc")
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

    public Category(Long categoryId, String name, String alias) {
        this.categoryId = categoryId;
        this.name = name;
        this.alias = alias;
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

    public static Category copyFull(Category category) {
        Category copyCategory = new Category();
        copyCategory.setCategoryId(category.getCategoryId());
        copyCategory.setName(category.getName());
        copyCategory.setImage(category.getImage());
        copyCategory.setAlias(category.getAlias());
        copyCategory.setEnabled(category.isEnabled());
        copyCategory.setHasChildren(category.getChildren().size() > 0);

        return copyCategory;
    }

    public static Category copyFull(Category category, String name) {
        Category copyCategory = Category.copyFull(category);
        copyCategory.setName(name);

        return copyCategory;
    }

    // getter method used in categories.html to show image of the category
    @Transient
    public String getImagePath() {
        if (this.categoryId == null) return "/images/image-thumbnail.png";

        return "/category-images/" + this.categoryId + "/" + this.image;
    }

    @Transient
    private boolean hasChildren;

    @Override
    public String toString() {
        return this.name;
    }
}
