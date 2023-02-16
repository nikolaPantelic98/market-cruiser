package com.marketcruiser.customer;

import com.marketcruiser.common.entity.Country;

import java.util.List;

public interface CustomerService {

    List<Country> listAllCountries();
    boolean isEmailUnique(String email);
}
