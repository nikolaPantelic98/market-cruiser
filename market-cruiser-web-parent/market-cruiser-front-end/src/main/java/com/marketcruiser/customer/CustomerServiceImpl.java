package com.marketcruiser.customer;

import com.marketcruiser.common.entity.AuthenticationType;
import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.exception.CustomerNotFoundException;
import com.marketcruiser.settings.CountryRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * This class represents the service layer for providing the business logic related to customer.
 */
@Service
public class CustomerServiceImpl implements CustomerService{

    private final CustomerRepository customerRepository;
    private final CountryRepository countryRepository;
    PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, CountryRepository countryRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.countryRepository = countryRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Returns a list of all countries in ascending order by name.
     */
    @Override
    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    /**
     * Checks whether a customer with the given email exists in the database.
     *
     * @param email the email address to check
     * @return true if the email is unique (i.e., not associated with any customer), false otherwise
     */
    @Override
    public boolean isEmailUnique(String email) {
        Customer customer = customerRepository.findCustomerByEmail(email);

        return customer == null;
    }

    /**
     * Registers a new customer in the database.
     *
     * @param customer the customer to register
     */
    @Override
    public void registerCustomer(Customer customer) {
        encodePassword(customer);
        customer.setEnabled(false);
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(AuthenticationType.DATABASE);

        String randomCode = RandomString.make(64);
        customer.setVerificationCode(randomCode);

        System.out.println("Verification code:  " + randomCode);

        customerRepository.save(customer);
    }

    /**
     * Encodes the customer's password using the password encoder.
     *
     * @param customer the customer whose password to encode
     */
    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }

    /**
     * Returns the customer with the given email address.
     *
     * @param email the email address of the customer to retrieve
     */
    @Override
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findCustomerByEmail(email);
    }

    /**
     * Verifies a customer's account with the given verification code.
     *
     * @param verificationCode the verification code to use
     * @return true if the verification is successful, false otherwise
     */
    @Override
    public boolean verifyCustomer(String verificationCode) {
        Customer customer = customerRepository.findCustomerByVerificationCode(verificationCode);

        if (customer == null || customer.isEnabled()) {
            return false;
        } else {
            customerRepository.enableCustomer(customer.getCustomerId());
            return true;
        }
    }

    /**
     * Updates a customer's authentication type if it is different from the given type.
     *
     * @param customer the customer to update
     * @param type the new authentication type
     */
    @Override
    @Transactional
    public void updateAuthenticationType(Customer customer, AuthenticationType type) {
        if (!customer.getAuthenticationType().equals(type)) {
            customerRepository.updateAuthenticationType(customer.getCustomerId(), type);
        }
    }

    /**
     * Adds a new customer to the customer repository upon successful OAuth login.
     *
     * @param name the name of the new customer
     * @param email the email of the new customer
     * @param countryCode the country code of the new customer
     */
    @Override
    public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode) {
        Customer customer = new Customer();
        customer.setEmail(email);
        setName(name, customer);

        customer.setEnabled(true);
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(AuthenticationType.GOOGLE);
        customer.setPassword("");
        customer.setAddressLine1("");
        customer.setAddressLine2("");
        customer.setCity("");
        customer.setState("");
        customer.setPhoneNumber("");
        customer.setPostCode("");
        customer.setCountry(countryRepository.findCountryByCode(countryCode));

        customerRepository.save(customer);
    }

    /**
     * Helper method to set the first and last name of a customer.
     *
     * @param name the full name of the customer
     * @param customer the customer object to update
     */
    private void setName(String name, Customer customer) {
        String[] nameArray = name.split(" ");

        if (nameArray.length < 2) {
            customer.setFirstName(name);
            customer.setLastName("");
        } else {
            String firstName = nameArray[0];
            customer.setFirstName(firstName);

            String lastName = name.replaceFirst(firstName, "");
            customer.setLastName(lastName);
        }
    }

    /**
     * Updates a customer's information.
     *
     * @param customerInForm the customer object with updated information
     */
    @Override
    public void updateCustomer(Customer customerInForm) {
        Customer customerInDB = customerRepository.findById(customerInForm.getCustomerId()).get();

        if (customerInDB.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
            if (!customerInForm.getPassword().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
                customerInForm.setPassword(encodedPassword);
            } else {
                customerInForm.setPassword(customerInDB.getPassword());
            }
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
     * Updates the reset password token for the customer with the provided email address.
     *
     * @param email the email of the customer
     * @return the new reset password token
     * @throws CustomerNotFoundException if no customer is found with the given email
     */
    @Override
    public String updateRestPasswordToken(String email) throws CustomerNotFoundException {
        Customer customer = customerRepository.findCustomerByEmail(email);
        if (customer != null) {
            String token = RandomString.make(30);
            customer.setResetPasswordToken(token);
            customerRepository.save(customer);

            return token;
        } else {
            throw new CustomerNotFoundException("Could not find any customer with the email " + email);
        }
    }

    /**
     * Retrieves the customer associated with the provided reset password token.
     *
     * @param token the reset password token used to retrieve the customer
     * @return the customer associated with the provided reset password token
     */
    @Override
    public Customer getCustomerByResetPasswordToken(String token) {
        return customerRepository.findByResetPasswordToken(token);
    }

    /**
     * Updates the password for the customer associated with the provided reset password token.
     *
     * @param token the reset password token used to retrieve the customer
     * @param newPassword the new password to update for the customer
     * @throws CustomerNotFoundException if no customer found for the given reset password token
     */
    @Override
    public void updatePassword(String token, String newPassword) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByResetPasswordToken(token);

        if (customer == null) {
            throw new CustomerNotFoundException("No customer found: invalid token");
        }

        customer.setPassword(newPassword);
        customer.setResetPasswordToken(null);
        encodePassword(customer);

        customerRepository.save(customer);
    }
}
