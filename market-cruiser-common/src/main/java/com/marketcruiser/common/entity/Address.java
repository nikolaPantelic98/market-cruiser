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
        name = "address"
)
public class Address {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long addressId;
    @Column(
            name = "first_name",
            nullable = false,
            length = 45
    )
    private String firstName;
    @Column(
            name = "last_name",
            nullable = false,
            length = 45
    )
    private String lastName;
    @Column(
            name = "phone_number",
            nullable = false,
            length = 15
    )
    private String phoneNumber;
    @Column(
            name = "address_line_1",
            nullable = false,
            length = 64
    )
    private String addressLine1;
    @Column(
            name = "address_line_2",
            length = 64
    )
    private String addressLine2;
    @Column(
            name = "city",
            nullable = false,
            length = 45
    )
    private String city;
    @Column(
            name = "state",
            nullable = false,
            length = 45
    )
    private String state;
    @Column(
            name = "post_code",
            nullable = false,
            length = 10
    )
    private String postCode;

    @ManyToOne
    @JoinColumn(
            name = "country_id"
    )
    private Country country;

    @ManyToOne
    @JoinColumn(
            name = "customer_id"
    )
    private Customer customer;

    @Column(
            name = "default_address"
    )
    private boolean defaultForShipping;


    @Override
    public String toString() {
        String address = firstName;

        if (lastName != null && !lastName.isEmpty()) {
            address += " " + lastName;
        }

        if (!addressLine1.isEmpty()) {
            address += ", " + addressLine1;
        }

        if (addressLine2 != null && !addressLine2.isEmpty()) {
            address += ", " + addressLine2;
        }

        if (!city.isEmpty()) {
            address += ", " + city;
        }

        if (state != null && !state.isEmpty()) {
            address += ", " + state;
        }

        address += ", " + country.getName();

        if (!postCode.isEmpty()) {
            address += ". Post Code: " + postCode;
        }

        if (!phoneNumber.isEmpty()) {
            address += ". Phone Number: " + phoneNumber;
        }

        return address;
    }
}
