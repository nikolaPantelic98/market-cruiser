package com.marketcruiser.common.entity.product;

import com.marketcruiser.common.entity.Brand;
import com.marketcruiser.common.entity.Category;
import com.marketcruiser.common.entity.Constants;
import lombok.*;

import javax.persistence.*;
import java.util.*;

/**
 * Represents a product entity with its attributes.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
            length = 255,
            nullable = false
    )
    private String name;
    @Column(
            name = "alias",
            unique = true,
            length = 255,
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

    @Column(
            name = "main_image",
            nullable = false
    )
    private String mainImage;

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

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<ProductImage> images = new HashSet<>();

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProductDetails> details = new ArrayList<>();

    public Product(Long productId) {
        this.productId = productId;
    }


    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                '}';
    }


    /**
     * Adds an extra product image to the list of images for this product.
     *
     * @param imageName the name of the image to add.
     */
    public void addExtraImage(String imageName) {
        this.images.add(new ProductImage(imageName, this));
    }

    /**
     * Gets the file path of the main image for this product.
     *
     * @return the file path of the main image for this product.
     */
    @Transient
    public String getMainImagePath() {
        if (productId == null || mainImage == null) return "/images/image-thumbnail.png";

        return Constants.S3_BASE_URI + "/product-images/" + this.productId + "/" + this.mainImage;
    }

    /**
     * Adds a product detail to the list of details for this product.
     *
     * @param name  the name of the product detail to add.
     * @param value the value of the product detail to add.
     */
    public void addDetail(String name, String value) {
        this.details.add(new ProductDetails(name, value, this));
    }

    /**
     * Adds a product detail with a given product ID to the list of details for this product.
     *
     * @param productId the ID of the product to add the detail for.
     * @param name      the name of the product detail to add.
     * @param value     the value of the product detail to add.
     */
    public void addDetail(Long productId, String name, String value) {
        this.details.add(new ProductDetails(productId, name, value, this));
    }

    /**
     * Method that checks for the presence of an image name in a list of product images.
     *
     * @param imageName the name of the image to check for.
     * @return true if the image is found, false otherwise.
     */
    public boolean containsImageName(String imageName) {
        Iterator<ProductImage> iterator = images.iterator();

        while (iterator.hasNext()) {
            ProductImage image = iterator.next();
            if (image.getName().equals(imageName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Method that returns a shortened version of the name attribute.
     *
     * @return a shortened version of the name attribute.
     */
    @Transient
    public String getShortName() {
        if (name.length() > 30) {
            return name.substring(0, 30).concat("...");
        } else {
            return name;
        }
    }

    /**
     * Method that returns the discount price of the product.
     *
     * @return the discount price of the product.
     */
    @Transient
    public float getDiscountPrice() {
        if (discountPercent > 0) {
            return price * ((100 - discountPercent) / 100);
        }
        return this.price;
    }
}
