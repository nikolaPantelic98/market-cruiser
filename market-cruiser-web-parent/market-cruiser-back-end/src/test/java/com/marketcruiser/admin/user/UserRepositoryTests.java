package com.marketcruiser.admin.user;

import com.marketcruiser.common.entity.Role;
import com.marketcruiser.common.entity.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;


    @Test
    @Disabled
    public void testCreateUserWithOneRole() {
        Role roleAdmin = entityManager.find(Role.class, 1L);
        User userAdmin = new User("admin@gmail.com", "admin123", "Nikola", "Pantelic");
        userAdmin.addRole(roleAdmin);

        User savedUser = userRepository.save(userAdmin);
        assertThat(savedUser.getUserId()).isGreaterThan(0);
    }

    @Test
    @Disabled
    public void testCreateNewUserWithTwoRoles() {
        User userJohn = new User("john@gmail.com", "john123", "John", "White");
        Role roleEditor = new Role(3L);
        Role roleAssistant = new Role(5L);
        userJohn.addRole(roleEditor);
        userJohn.addRole(roleAssistant);

        User savedUser = userRepository.save(userJohn);
        assertThat(savedUser.getUserId()).isGreaterThan(0);
    }

    @Test
    @Disabled
    public void testListAllUsers() {
        Iterable<User> listUsers = userRepository.findAll();
        listUsers.forEach(user -> System.out.println(user));
    }

    @Test
    @Disabled
    public void testGetUserById() {
        User user = userRepository.findById(1L).get();
        System.out.println(user);
        assertThat(user).isNotNull();
    }

    @Test
    @Disabled
    public void testUpdateUserDetails() {
        User userNikola = userRepository.findById(1L).get();
        userNikola.setEnabled(true);

        userRepository.save(userNikola);
    }

    @Test
    @Disabled
    public void testUpdateUserRoles() {
        User userJohn = userRepository.findById(2L).get();
        Role roleEditor = new Role(3L);
        Role roleSalesperson = new Role(2L);

        userJohn.getRoles().remove(roleEditor);
        userJohn.addRole(roleSalesperson);

        userRepository.save(userJohn);
    }

    @Test
    @Disabled
    public void deleteUser() {
        Long userId = 2L;
        userRepository.deleteById(userId);
    }

    @Test
    @Disabled
    public void testGetUserByEmailAddress() {
        String emailAddress = "natasa@gmail.com";
        User user = userRepository.getUserByEmailAddress(emailAddress);

        assertThat(user).isNotNull();
    }

    @Test
    @Disabled
    public void testCountById() {
        Long userId = 1L;
        Long countById = userRepository.countByUserId(userId);

        assertThat(countById).isNotNull().isGreaterThan(0);
    }

    @Test
    @Disabled
    public void testDisableUser() {
        Long userId = 3L;
        userRepository.updateEnabledStatus(userId, false);
    }

    @Test
    @Disabled
    public void testEnableUser() {
        Long userId = 2L;
        userRepository.updateEnabledStatus(userId, true);
    }

    @Test
    @Disabled
    public void testSearchUsers() {
        String keyword = "nikola";

        int pageNumber = 0;
        int pageSize = 4;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = userRepository.findAllUsers(keyword, pageable);

        List<User> listUsers = page.getContent();

        listUsers.forEach(user -> System.out.println(user));

        assertThat(listUsers.size()).isGreaterThan(0);
    }
}
