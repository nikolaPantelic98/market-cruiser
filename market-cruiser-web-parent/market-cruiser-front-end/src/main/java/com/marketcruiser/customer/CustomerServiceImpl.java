package com.marketcruiser.customer;

import com.marketcruiser.common.entity.AuthenticationType;
import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.settings.CountryRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

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


    // returns a list of all countries in ascending order by name
    @Override
    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    // checks whether a customer with the given email exists in the database
    @Override
    public boolean isEmailUnique(String email) {
        Customer customer = customerRepository.findCustomerByEmail(email);

        return customer == null;
    }

    // registers a new customer in the database
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

    @Override
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findCustomerByEmail(email);
    }

    // verifies a customer's account with the given verification code
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

    // updates a customer's authentication type if it is different from the given type
    @Override
    @Transactional
    public void updateAuthenticationType(Customer customer, AuthenticationType type) {
        if (!customer.getAuthenticationType().equals(type)) {
            customerRepository.updateAuthenticationType(customer.getCustomerId(), type);
        }
    }

    // adds a new customer to the customer repository upon successful OAuth login
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

    // helper method to set the first and last name of a customer
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

    // updates a customer's information
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

        customerRepository.save(customerInForm);
    }

    // encodes the customer's password using the password encoder
    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }
}
