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


    @Transient
    public String getLogoPath() {
        if (this.brandId == null) return "/images/image-thumbnail.png";

        return "/brand-logos/" + this.brandId + "/" + this.logo;
    }
}
