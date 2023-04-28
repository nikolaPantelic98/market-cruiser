package com.marketcruiser.security;

import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * A custom UserDetailsService implementation to load a user by email address from the CustomerRepository.
 */
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;


    /**
     * Loads a user by the given email address.
     *
     * @param email The email address of the user to load.
     * @return A UserDetails object representing the loaded user.
     * @throws UsernameNotFoundException If no customer is found with the given email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findCustomerByEmail(email);

        if (customer == null) {
            throw new UsernameNotFoundException("No customer found with email " + email);
        }

        return new CustomerUserDetails(customer);
    }
}
