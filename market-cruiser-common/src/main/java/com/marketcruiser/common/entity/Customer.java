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
}
