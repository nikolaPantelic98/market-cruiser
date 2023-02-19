package com.marketcruiser.admin.customer;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.exception.CustomerNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomerService {

    Page<Customer> listCustomersByPage(int pageNumber, String sortField, String sortDir, String keyword);
    void updateCustomerEnabledStatus(Long customerId, boolean enabled);
    Customer getCustomer(Long customerId) throws CustomerNotFoundException;
    List<Country> listAllCountries();
    boolean isEmailUnique(Long customerId, String email);
    void saveCustomer(Customer customerInForm);
    void deleteCustomer(Long customerId) throws CustomerNotFoundException;
}
