package com.marketcruiser.common.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class represents a user entity in the application.
 * It is used to store user information such as email, password, name, and photo.
 * The user can be associated with multiple roles using the roles set.
 * The class provides methods for adding roles, getting user's full name and photo path,
 * and checking if a user has a specific role.
 */
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

    /**
     * Adds a role to the user's roles set.
     * @param role the role to be added.
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * Gets the path to the user's photo image file.
     * @return the path to the user's photo image file.
     */
    @Transient
    public String getPhotosImagePath() {
        if (userId == null || photo == null) return "/images/default-user.png";

        return Constants.S3_BASE_URI + "/user-photos/" + this.userId + "/" + this.photo;
    }

    /**
     * Gets the full name of the user.
     */
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Checks if the user has the specified role.
     * @param roleName the name of the role to check.
     * @return true if the user has the specified role, false otherwise.
     */
    public boolean hasRole(String roleName) {
        Iterator<Role> iterator = roles.iterator();

        while (iterator.hasNext()) {
            Role role = iterator.next();
            if (role.getName().equals(roleName)) {
                return true;
            }
        }

        return false;
    }
}
