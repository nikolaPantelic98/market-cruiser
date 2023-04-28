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

/**
 * This class implements the {@link  UserService} interface and defines the business logic for user operations.
 * It contains methods to retrieve and manipulate User objects from the database.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService{

    public static final int USERS_PER_PAGE = 10;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Returns the user with the given email address.
     *
     * @param emailAddress email address of the user to be retrieved
     * @return user object with the given email address
     */
    @Override
    public User getUserByEmailAddress(String emailAddress) {
        return userRepository.getUserByEmailAddress(emailAddress);
    }

    /**
     * Returns a list of all users, sorted by first name in ascending order.
     */
    @Override
    public List<User> getAllUsersSortedByFirstName() {
        return userRepository.findAll(Sort.by("firstName").ascending());
    }

    /**
     * Returns a page of users sorted by the specified field and direction.
     *
     * @param pageNumber the page number
     * @param sortField the field to sort by
     * @param sortDir the sort direction
     * @param keyword the search keyword
     * @return page of users sorted by the specified field and direction
     */
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

    /**
     * Returns a list of all roles.
     */
    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Saves or updates a user.
     *
     * @param user the user to be saved or updated
     * @return saved or updated user
     */
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

    /**
     * Updates the user's account details in the database.
     *
     * @param userInForm the user whose details are to be updated
     * @return updated user object
     */
    @Override
    public User updateAccount(User userInForm) {
        User userInDB = userRepository.findById(userInForm.getUserId()).get();

        if (!userInForm.getPassword().isEmpty()) {
            userInDB.setPassword(userInForm.getPassword());
            encodePassword(userInDB);
        }

        if (userInForm.getPhoto() != null) {
            userInDB.setPhoto(userInForm.getPhoto());
        }

        userInDB.setFirstName(userInForm.getFirstName());
        userInDB.setLastName(userInForm.getLastName());

        return userRepository.save(userInDB);
    }

    /**
     * Encodes a user's password using the password encoder.
     *
     * @param user the user whose password needs to be encoded
     */
    @Override
    public void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    /**
     * Checks whether an email address is available for a new user.
     *
     * @param userId the user id of the user (if any)
     * @param emailAddress the email address to be checked
     * @return true if the email address is unique, false otherwise
     */
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

    /**
     * Gets a user by user id.
     *
     * @param userId the user id of the user to be retrieved
     * @return the user with the specified user id
     * @throws UserNotFoundException if the user cannot be found
     */
    @Override
    public User getUserById(Long userId) throws UserNotFoundException {
        try {
            return userRepository.findById(userId).get();
        } catch (NoSuchElementException exception) {
            throw new UserNotFoundException("Could not find any user with ID: " + userId);
        }
    }

    /**
     * Deletes a user by user id.
     *
     * @param userId the user id of the user to be deleted
     * @throws UserNotFoundException if the user cannot be found
     */
    @Override
    public void deleteUser(Long userId) throws UserNotFoundException {
        Long countById = userRepository.countByUserId(userId);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("Could not find any user with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    /**
     * Updates the enabled status of a user.
     *
     * @param userId the user id of the user whose enabled status needs to be updated
     * @param enabled the new enabled status
     */
    @Override
    public void updateUserEnabledStatus(Long userId, boolean enabled) {
        userRepository.updateEnabledStatus(userId, enabled);
    }
}