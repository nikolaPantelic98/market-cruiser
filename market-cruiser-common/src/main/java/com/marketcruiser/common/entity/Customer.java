package com.marketcruiser.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "customers"
)
public class Customer {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long customerId;
    @Column(
            name = "email",
            nullable = false,
            unique = true,
            length = 45
    )
    private String email;
    @Column(
            name = "password",
            nullable = false,
            length = 64
    )
    private String password;
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
    @Column(
            name = "verification_code",
            length = 64
    )
    private String verificationCode;
    @Column(
            name = "enabled"
    )
    private boolean enabled;
    @Column(
            name = "created_time"
    )
    private Date createdTime;

    @ManyToOne
    @JoinColumn(
            name = "country_id"
    )
    private Country country;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "authentication_type",
            length = 10
    )
    private AuthenticationType authenticationType;

    @Column(
            name = "reset_password_token",
            length = 30
    )
    private String resetPasswordToken;


    public Customer(Long customerId) {
        this.customerId = customerId;
    }


    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Transient
    public String getAddress() {
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
