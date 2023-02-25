package com.marketcruiser.admin.customer;

import com.marketcruiser.admin.settings.country.CountryRepository;
import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService{

    public static final int CUSTOMERS_PER_PAGE = 10;

    private final CustomerRepository customerRepository;
    private final CountryRepository countryRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, CountryRepository countryRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // this method implements pagination and sorting of customers based on multiple search criteria
    @Override
    public Page<Customer> listCustomersByPage(int pageNumber, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, CUSTOMERS_PER_PAGE, sort);

        if (keyword != null) {
            return customerRepository.findAllCustomers(keyword, pageable);
        }

        return customerRepository.findAll(pageable);
    }

    @Override
    public void updateCustomerEnabledStatus(Long customerId, boolean enabled) {
        customerRepository.updateCustomerEnabledStatus(customerId, enabled);
    }

    @Override
    public Customer getCustomer(Long customerId) throws CustomerNotFoundException {
        try {
            return customerRepository.findById(customerId).get();
        } catch (NoSuchElementException exception) {
            throw new CustomerNotFoundException("Could not find any customers with ID " + customerId);
        }
    }

    @Override
    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    // checks if the given email is unique
    @Override
    public boolean isEmailUnique(Long customerId, String email) {
        Customer existCustomer = customerRepository.findCustomerByEmail(email);

        if (existCustomer != null && existCustomer.getCustomerId() != customerId) {
            // found another customer having the same email
            return false;
        }

        return true;
    }

    // saves customer
    @Override
    public void saveCustomer(Customer customerInForm) {
        Customer customerInDB = customerRepository.findById(customerInForm.getCustomerId()).get();

        if (!customerInForm.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
            customerInForm.setPassword(encodedPassword);
        } else {
            customerInForm.setPassword(customerInDB.getPassword());
        }

        customerInForm.setEnabled(customerInDB.isEnabled());
        customerInForm.setCreatedTime(customerInDB.getCreatedTime());
        customerInForm.setVerificationCode(customerInDB.getVerificationCode());
        customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());

        customerRepository.save(customerInForm);
    }

    // deletes customer
    @Override
    public void deleteCustomer(Long customerId) throws CustomerNotFoundException {
        Long count = customerRepository.countByCustomerId(customerId);
        if (count == null || count == 0) {
            throw new CustomerNotFoundException("Could not find any customers with ID " + customerId);
        }

        customerRepository.deleteById(customerId);
    }
}
