package com.marketcruiser.common.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "countries"
)
public class Country {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long countryId;
    @Column(
            name = "name",
            nullable = false,
            length = 45
    )
    private String name;
    @Column(
            name = "code",
            nullable = false,
            length = 5
    )
    private String code;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @OneToMany(
            mappedBy = "country",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<State> states;


    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public Country(String name) {
        this.name = name;
    }

    public Country(Long countryId, String name, String code) {
        this.countryId = countryId;
        this.name = name;
        this.code = code;
    }

    public Country(Long countryId) {
        this.countryId = countryId;
    }

    @Override
    public String toString() {
        return "Country{" +
                "countryId=" + countryId +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
