package com.marketcruiser.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "currency"
)
public class Currency {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long currencyId;
    @Column(
            name = "name",
            nullable = false,
            length = 64
    )
    private String name;
    @Column(
            name = "symbol",
            nullable = false,
            length = 3
    )
    private String symbol;
    @Column(
            name = "code",
            nullable = false,
            length = 4
    )
    private String code;


    public Currency(String name, String symbol, String code) {
        this.name = name;
        this.symbol = symbol;
        this.code = code;
    }


    @Override
    public String toString() {
        return name + " - " + code + " - " + symbol;
    }
}
