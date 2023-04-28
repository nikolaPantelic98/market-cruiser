package com.marketcruiser.admin.security;

import com.marketcruiser.admin.user.UserRepository;
import com.marketcruiser.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * MarketCruiserUserDetailsService is a class that implements the Spring Security UserDetailsService interface
 * and is responsible for loading user details by email address from the UserRepository.
 */
public class MarketCruiserUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads the user details for a given email address by using UserRepository.
     *
     * @param emailAddress email address of the user
     * @return UserDetails object that contains the user details
     * @throws UsernameNotFoundException thrown when user with the given email address is not found in the UserRepository
     */
    @Override
    public UserDetails loadUserByUsername(String emailAddress) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmailAddress(emailAddress);

        if (user != null) {
            return new MarketCruiserUserDetails(user);
        }

        throw new UsernameNotFoundException("Could not find user with email: " + emailAddress);
    }
}
