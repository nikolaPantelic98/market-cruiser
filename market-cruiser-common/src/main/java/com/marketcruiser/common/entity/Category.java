package com.marketcruiser.common.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a category of products in the system.
 * A category can have a parent category and multiple children categories.
 * Each category has a name, alias, image, and an enabled status.
 * The category entity is persisted in the database and uses JPA annotations.
 */
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

    /**
     * Creates a copy of the given category with only its ID and name set.
     *
     * @param category The category to copy.
     * @return A new category with only its ID and name set.
     */
    public static Category copyCategoryIdAndName(Category category) {
        Category copyCategory = new Category();
        copyCategory.setCategoryId(category.getCategoryId());
        copyCategory.setName(category.getName());

        return copyCategory;
    }

    /**
     * Creates a new category with the given ID and name.
     *
     * @param categoryId The ID of the category.
     * @param name       The name of the category.
     * @return A new category with the given ID and name.
     */
    public static Category copyCategoryIdAndName(Long categoryId, String name) {
        Category copyCategory = new Category();
        copyCategory.setCategoryId(categoryId);
        copyCategory.setName(name);

        return copyCategory;
    }

    /**
     * Creates a copy of the given category with all of its properties set.
     *
     * @param category The category to copy.
     * @return A new category with all of the given category's properties set.
     */
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

    /**
     * Creates a copy of the given category with the given name and all other properties set.
     *
     * @param category The category to copy.
     * @param name     The name for the new category.
     * @return A new category with the given name and all other properties set from the original category.
     */
    public static Category copyFull(Category category, String name) {
        Category copyCategory = Category.copyFull(category);
        copyCategory.setName(name);

        return copyCategory;
    }

    /**
     * Returns the image path for this category's image.
     */
    @Transient
    public String getImagePath() {
        if (this.categoryId == null) return "/images/image-thumbnail.png";

        return Constants.S3_BASE_URI + "/category-images/" + this.categoryId + "/" + this.image;
    }

    /**
     * Indicates if this category has any child categories.
     */
    @Transient
    private boolean hasChildren;

    @Override
    public String toString() {
        return this.name;
    }
}
