package com.marketcruiser.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * The State class represents a state or province in a country.
 * It contains the state's ID, name, and the country it belongs to.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "states"
)
public class State {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long stateId;
    @Column(
            name = "name",
            nullable = false,
            length = 45
    )
    private String name;

    @ManyToOne
    @JoinColumn(
            name = "country_id"
    )
    private Country country;


    public State(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    @Override
    public String toString() {
        return "State{" +
                "stateId=" + stateId +
                ", name='" + name + '\'' +
                ", country=" + country +
                '}';
    }
}
