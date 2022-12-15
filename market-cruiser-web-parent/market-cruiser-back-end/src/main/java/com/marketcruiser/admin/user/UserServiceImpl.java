package com.marketcruiser.admin.user;

import com.marketcruiser.common.entity.Role;
import com.marketcruiser.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    public static final int USERS_PER_PAGE = 5;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // returns a list of all users, sorted by first name in ascending order
    @Override
    public List<User> getAllUsersSortedByFirstName() {
        return userRepository.findAll(Sort.by("firstName").ascending());
    }

    // returns a page of users sorted by the specified field and direction
    @Override
    public Page<User> listUsersByPage(int pageNumber, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);

        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, USERS_PER_PAGE, sort);

        if (keyword != null) {
            return userRepository.findAllUsers(keyword, pageable);
        }

        return userRepository.findAll(pageable);
    }

    // returns a list of all roles
    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // saves or updates a user
    @Override
    public User saveUser(User user) {
        boolean isUpdatingUser = (user.getUserId() != null);

        if (isUpdatingUser) {
            User existingUser = userRepository.findById(user.getUserId()).get();

            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                encodePassword(user);
            }

        } else {
            encodePassword(user);
        }
        return userRepository.save(user);
    }

    // encodes a user's password
    @Override
    public void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    // checks whether an email address is available for a new user
    @Override
    public boolean isEmailAddressUnique(Long userId, String emailAddress) {
        User userByEmailAddress = userRepository.getUserByEmailAddress(emailAddress);

        if (userByEmailAddress == null) return true;

        boolean isCreatingNew = (userId == null);

        if (isCreatingNew) {
            if (userByEmailAddress != null) return false;
        } else {
            if (userByEmailAddress.getUserId() != userId) {
                return false;
            }
        }
        return true;
    }

    // gets a user by user id
    @Override
    public User getUserById(Long userId) throws UserNotFoundException {
        try {
            return userRepository.findById(userId).get();
        } catch (NoSuchElementException exception) {
            throw new UserNotFoundException("Could not find any user with ID: " + userId);
        }
    }

    // deletes a user by user id
    @Override
    public void deleteUser(Long userId) throws UserNotFoundException {
        Long countById = userRepository.countByUserId(userId);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("Could not find any user with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public void updateUserEnabledStatus(Long userId, boolean enabled) {
        userRepository.updateEnabledStatus(userId, enabled);
    }
}
