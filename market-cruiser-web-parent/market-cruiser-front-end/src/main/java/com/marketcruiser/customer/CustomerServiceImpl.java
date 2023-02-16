package com.marketcruiser.customer;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.settings.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;
    private final CountryRepository countryRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, CountryRepository countryRepository) {
        this.customerRepository = customerRepository;
        this.countryRepository = countryRepository;
    }


    @Override
    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    @Override
    public boolean isEmailUnique(String email) {
        Customer customer = customerRepository.findCustomerByEmail(email);

        return customer == null;
    }
}
