package com.marketcruiser.admin.user;

import com.marketcruiser.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.emailAddress = :emailAddress")
    User getUserByEmailAddress(@Param("emailAddress") String emailAddress);

    @Query("SELECT u FROM User u WHERE CONCAT(u.userId, ' ', u.emailAddress, ' ', u.firstName, ' ', u.lastName) LIKE %?1%")
    Page<User> findAllUsers(String keyword, Pageable pageable);

    Long countByUserId(Long userId);

    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.userId = ?1")
    @Modifying
    void updateEnabledStatus(Long userId, boolean enabled);
}
