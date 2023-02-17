package com.marketcruiser.customer;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.Customer;

import java.util.List;

public interface CustomerService {

    List<Country> listAllCountries();
    boolean isEmailUnique(String email);
    void registerCustomer(Customer customer);
}
