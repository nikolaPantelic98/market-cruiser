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
@ToString
@Builder
@Table(
        name = "users"
)
public class User {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long userId;
    @Column(
            name = "email_address",
            length = 128,
            nullable = false,
            unique = true
    )
    private String emailAddress;
    @Column(
            name = "password",
            length = 64,
            nullable = false
    )
    private String password;
    @Column(
            name = "first_name",
            length = 45,
            nullable = false
    )
    private String firstName;
    @Column(
            name = "last_name",
            length = 45,
            nullable = false
    )
    private String lastName;
    @Column(
            name = "photo",
            length = 64
    )
    private String photo;
    @Column(
            name = "enabled"
    )
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id"
            )
    )
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();


    public User(String emailAddress, String password, String firstName, String lastName) {
        this.emailAddress = emailAddress;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String emailAddress, String password, String firstName, String lastName, boolean enabled) {
        this.emailAddress = emailAddress;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    // getter method used in users.html and user_form.html to show profile photo of the user
    @Transient
    public String getPhotosImagePath() {
        if (userId == null || photo == null) return "/images/default-user.png";

        return "/user-photos/" + this.userId + "/" + this.photo;
    }

    // getter method used in users.html to show full name of the user on small screen
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
