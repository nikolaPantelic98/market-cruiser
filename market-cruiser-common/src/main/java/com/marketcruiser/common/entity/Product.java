package com.marketcruiser.common.entity;

import lombok.*;

import javax.persistence.*;
import java.util.*;

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


    // adds an extra product image to the list of images for this product.
    public void addExtraImage(String imageName) {
        this.images.add(new ProductImage(imageName, this));
    }

    // gets the file path of the main image for this product
    @Transient
    public String getMainImagePath() {
        if (productId == null || mainImage == null) return "/images/image-thumbnail.png";

        return "/product-images/" + this.productId + "/" + this.mainImage;
    }

    // adds a product detail to the list of details for this product
    public void addDetail(String name, String value) {
        this.details.add(new ProductDetails(name, value, this));
    }

    public void addDetail(Long productId, String name, String value) {
        this.details.add(new ProductDetails(productId, name, value, this));
    }

    // method that checks for the presence of an image name in a list of product images
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

    // method that returns a shortened version of the name attribute
    @Transient
    public String getShortName() {
        if (name.length() > 30) {
            return name.substring(0, 30).concat("...");
        } else {
            return name;
        }
    }

    // method that returns discount price
    @Transient
    public float getDiscountPrice() {
        if (discountPercent > 0) {
            return price * ((100 - discountPercent) / 100);
        }
        return this.price;
    }
}
