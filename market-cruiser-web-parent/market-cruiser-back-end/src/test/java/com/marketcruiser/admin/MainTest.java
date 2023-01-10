package com.marketcruiser.admin;

import com.marketcruiser.admin.user.RoleRepository;
import com.marketcruiser.admin.user.UserRepository;
import com.marketcruiser.common.entity.Role;
import com.marketcruiser.common.entity.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class MainTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TestEntityManager entityManager;


    @Test
    @Disabled
    public void testCreateFirstUserAdmin() {
        Role roleAdmin = new Role("Admin", "Manage everything");
        roleRepository.save(roleAdmin);

        Role role = entityManager.find(Role.class, 1L);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        User userAdmin = new User("admin@gmail.com", encodedPassword, "Nikola", "Pantelic", true);

        userAdmin.addRole(role);

        User savedUser = userRepository.save(userAdmin);

        assertThat(savedUser.getUserId()).isGreaterThan(0);
    }
}
