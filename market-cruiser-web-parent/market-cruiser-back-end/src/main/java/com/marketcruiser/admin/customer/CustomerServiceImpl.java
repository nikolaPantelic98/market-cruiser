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

/**
 * This class implements the {@link  CustomerService} interface and defines the business logic for customer operations.
 * It contains methods to retrieve and manipulate Customer objects from the database.
 */
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


    /**
     * Retrieves a paginated list of customers based on multiple search criteria.
     *
     * @param pageNumber The page number to retrieve.
     * @param sortField The field to sort the results by.
     * @param sortDir The sort direction (ascending or descending).
     * @param keyword The keyword to search for.
     * @return A paginated list of customers.
     */
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

    /**
     * Updates the enabled status of a customer.
     *
     * @param customerId The ID of the customer to update.
     * @param enabled The new enabled status of the customer.
     */
    @Override
    public void updateCustomerEnabledStatus(Long customerId, boolean enabled) {
        customerRepository.updateCustomerEnabledStatus(customerId, enabled);
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param customerId The ID of the customer to retrieve.
     * @return The customer entity.
     * @throws CustomerNotFoundException if a customer with the given ID is not found.
     */
    @Override
    public Customer getCustomer(Long customerId) throws CustomerNotFoundException {
        try {
            return customerRepository.findById(customerId).get();
        } catch (NoSuchElementException exception) {
            throw new CustomerNotFoundException("Could not find any customers with ID " + customerId);
        }
    }

    /**
     * Retrieves a list of all countries in the system, ordered by name.
     */
    @Override
    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    /**
     * Checks if the given email is unique.
     *
     * @param customerId The ID of the customer to exclude from the check.
     * @param email The email to check.
     * @return true if the email is unique, false otherwise.
     */
    @Override
    public boolean isEmailUnique(Long customerId, String email) {
        Customer existCustomer = customerRepository.findCustomerByEmail(email);

        if (existCustomer != null && existCustomer.getCustomerId() != customerId) {
            // found another customer having the same email
            return false;
        }

        return true;
    }

    /**
     * Saves a customer to the database.
     * @param customerInForm The customer entity to save.
     */
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
        customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());

        customerRepository.save(customerInForm);
    }

    /**
     * Deletes a customer with the given customerId from the database.
     *
     * @param customerId The ID of the customer to be deleted.
     * @throws CustomerNotFoundException If the customer with the given ID does not exist in the database.
     */
    @Override
    public void deleteCustomer(Long customerId) throws CustomerNotFoundException {
        Long count = customerRepository.countByCustomerId(customerId);
        if (count == null || count == 0) {
            throw new CustomerNotFoundException("Could not find any customers with ID " + customerId);
        }

        customerRepository.deleteById(customerId);
    }
}
