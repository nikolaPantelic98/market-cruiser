package com.marketcruiser.admin.user;

import com.marketcruiser.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * The UserRepository interface defines the methods to interact with the {@link User} entity in the database.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * This method finds a User entity by email address.
     *
     * @param emailAddress the email address to search for
     * @return the User entity matching the given email address, if found
     */
    @Query("SELECT u FROM User u WHERE u.emailAddress = :emailAddress")
    User getUserByEmailAddress(@Param("emailAddress") String emailAddress);

    /**
     * This method returns a page of User entities that match the given keyword.
     * The search is performed on the user ID, email address, first name, and last name.
     *
     * @param keyword the keyword to search for
     * @param pageable the Pageable object containing pagination information
     * @return a page of User entities matching the given keyword
     */
    @Query("SELECT u FROM User u WHERE CONCAT(u.userId, ' ', u.emailAddress, ' ', u.firstName, ' ', u.lastName) LIKE %?1%")
    Page<User> findAllUsers(String keyword, Pageable pageable);

    /**
     * This method counts the number of User entities with the given ID.
     *
     * @param userId the ID of the user to count
     * @return the number of User entities with the given ID
     */
    Long countByUserId(Long userId);

    /**
     * This method updates the enabled status of a User entity.
     *
     * @param userId the ID of the user to update
     * @param enabled the new value for the enabled status
     */
    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.userId = ?1")
    @Modifying
    void updateEnabledStatus(Long userId, boolean enabled);
}
