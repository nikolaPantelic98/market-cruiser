package com.marketcruiser.admin.security;

import com.marketcruiser.admin.user.UserRepository;
import com.marketcruiser.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class MarketCruiserUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // loads the user details for a given email address
    @Override
    public UserDetails loadUserByUsername(String emailAddress) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmailAddress(emailAddress);

        if (user != null) {
            return new MarketCruiserUserDetails(user);
        }

        throw new UsernameNotFoundException("Could not find user with email: " + emailAddress);
    }
}
