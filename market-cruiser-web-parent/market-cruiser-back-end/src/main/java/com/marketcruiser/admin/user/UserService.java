package com.marketcruiser.admin.user;

import com.marketcruiser.common.entity.Role;
import com.marketcruiser.common.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    User getUserByEmailAddress(String emailAddress);
    List<User> getAllUsersSortedByFirstName();
    Page<User> listUsersByPage(int pageNumber, String sortField, String sortDir, String keyword);
    List<Role> getAllRoles();
    User saveUser(User user);
    User updateAccount(User userInForm);
    void encodePassword(User user);
    boolean isEmailAddressUnique(Long userId, String emailAddress);
    User getUserById(Long userId) throws UserNotFoundException;
    void deleteUser(Long userId) throws UserNotFoundException;
    void updateUserEnabledStatus(Long userId, boolean enabled);
}
