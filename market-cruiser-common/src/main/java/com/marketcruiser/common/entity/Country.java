package com.marketcruiser.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
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

    @OneToMany(mappedBy = "country")
    private Set<State> states;


    public Country(String name, String code) {
        this.name = name;
        this.code = code;
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
