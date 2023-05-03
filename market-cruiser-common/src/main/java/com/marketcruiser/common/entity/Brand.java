package com.marketcruiser.common.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The Brand class represents a brand in the system, and contains information about the brand name and logo.
 * It also has a many-to-many relationship with the Category class, representing the categories that the brand belongs to.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private Set<Category> categories = new HashSet<>();


    public Brand(String name) {
        this.name = name;
        this.logo = "brand-logo.png";
    }

    public Brand(Long brandId, String name) {
        this.brandId = brandId;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Brand [id=" + brandId + ", name=" + name + ", categories=" + categories + "]";
    }

    /**
     * Returns the path to the logo image file for the brand.
     * If the brand ID is null, a default thumbnail image is returned.
     */
    @Transient
    public String getLogoPath() {
        if (this.brandId == null) return "/images/image-thumbnail.png";

        return Constants.S3_BASE_URI + "/brand-logos/" + this.brandId + "/" + this.logo;
    }
}
