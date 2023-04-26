package com.marketcruiser.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

/**
 * The ShippingRate class represents a shipping rate entity, which includes the shipping cost, delivery time,
 * and whether or not the shipping supports cash on delivery.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "shipping_rates"
)
public class ShippingRate {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long shippingRateId;
    @Column(
            name = "rate"
    )
    private float rate;
    @Column(
            name = "days"
    )
    private int days;

    @Column(
            name = "cod_supported"
    )
    private boolean codSupported;

    @ManyToOne
    @JoinColumn(
            name = "country_id"
    )
    private Country country;

    @Column(
            name = "state",
            nullable = false,
            length = 45
    )
    private String state;


    @Override
    public String toString() {
        return "ShippingRate{" +
                "shippingRateId=" + shippingRateId +
                ", rate=" + rate +
                ", days=" + days +
                ", codSupported=" + codSupported +
                ", country=" + country +
                ", state='" + state + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ShippingRate other = (ShippingRate) obj;
        if (shippingRateId == null) {
            if (other.shippingRateId != null)
                return false;
        } else if (!shippingRateId.equals(other.shippingRateId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((shippingRateId == null) ? 0 : shippingRateId.hashCode());
        return result;
    }
}
