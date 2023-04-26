package com.marketcruiser.admin.user;

import com.marketcruiser.common.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The RoleRepository interface defines the methods to interact with the {@link Role} entity in the database.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
