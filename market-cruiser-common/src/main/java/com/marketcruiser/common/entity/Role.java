package com.marketcruiser.common.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

/**
 * The Role class represents the role entity that is stored in the "roles" table of the database.
 * It contains information about a user's role, including its name and description.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Table(
        name = "roles"
)
public class Role {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long roleId;
    @Column(
            name = "name",
            length = 40,
            nullable = false,
            unique = true
    )
    private String name;
    @Column(
            name = "description",
            length = 150,
            nullable = false
    )
    private String description;


    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Role(Long roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(roleId, role.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId);
    }
}
