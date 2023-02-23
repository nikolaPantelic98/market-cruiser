package com.marketcruiser.security;

import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findCustomerByEmail(email);

        if (customer == null) {
            throw new UsernameNotFoundException("No customer found with email " + email);
        }

        return new CustomerUserDetails(customer);
    }
}
