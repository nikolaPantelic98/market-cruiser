package com.marketcruiser.customer;

import com.marketcruiser.common.entity.AuthenticationType;
import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TestEntityManager entityManager;


    @Test
    public void testCreateCustomer1() {
        Long countryId = 2L; // United States of America
        Country country = entityManager.find(Country.class, countryId);

        Customer customer = new Customer();
        customer.setCountry(country);
        customer.setFirstName("Viktor");
        customer.setLastName("Brown");
        customer.setPassword("viktor123");
        customer.setEmail("viktor@gmail.com");
        customer.setPhoneNumber("386-274-4903");
        customer.setAddressLine1("8 Harvey Street");
        customer.setCity("Miami");
        customer.setState("Florida");
        customer.setPostCode("33174");

        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getCustomerId()).isGreaterThan(0);
    }

    @Test
    public void testCreateCustomer2() {
        Long countryId = 6L; // United Kingdom
        Country country = entityManager.find(Country.class, countryId);

        Customer customer = new Customer();
        customer.setCountry(country);
        customer.setFirstName("Bojan");
        customer.setLastName("White");
        customer.setPassword("bojan123");
        customer.setEmail("bojan@gmail.com");
        customer.setPhoneNumber("+441632960711");
        customer.setAddressLine1("50 Corby Craig Avenue");
        customer.setCity("Midlothian");
        customer.setState("Scotland");
        customer.setPostCode("EH25 9TL");
        customer.setCreatedTime(new Date());

        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getCustomerId()).isGreaterThan(0);
    }

    @Test
    public void testListCustomers() {
        Iterable<Customer> customers = customerRepository.findAll();
        customers.forEach(System.out :: println);

        assertThat(customers).hasSizeGreaterThan(1);
    }

    @Test
    public void testUpdateCustomer() {
        Long customerId = 2L;
        String lastName = "Russel";

        Customer customer = customerRepository.findById(customerId).get();
        customer.setLastName(lastName);
        customer.setEnabled(true);

        Customer updatedCustomer = customerRepository.save(customer);
        assertThat(updatedCustomer.getLastName()).isEqualTo(lastName);
    }

    @Test
    public void testGetCustomer() {
        Long customerId = 2L;
        Optional<Customer> findById = customerRepository.findById(customerId);

        assertThat(findById).isPresent();

        Customer customer = findById.get();
        System.out.println(customer);
    }

    @Test
    public void testDeleteCustomer() {
        Long customerId = 2L;
        customerRepository.deleteById(customerId);

        Optional<Customer> findById = customerRepository.findById(customerId);
        assertThat(findById).isNotPresent();
    }

    @Test
    public void testFindByEmail() {
        String email = "viktor@gmail.com";
        Customer customer = customerRepository.findCustomerByEmail(email);

        assertThat(customer).isNotNull();
        System.out.println(customer);
    }

    @Test
    public void testFindByVerificationCode() {
        String verificationCode = "code_123";
        Customer customer = customerRepository.findCustomerByVerificationCode(verificationCode);

        assertThat(customer).isNotNull();
        System.out.println(customer);
    }

    @Test
    public void testEnableCustomer() {
        Long customerId = 1L;
        customerRepository.enableCustomer(customerId);

        Customer customer = customerRepository.findById(customerId).get();
        assertThat(customer.isEnabled()).isTrue();
    }

    @Test
    public void testUpdateAuthenticationType() {
        Long customerId = 8L;
        customerRepository.updateAuthenticationType(customerId, AuthenticationType.GOOGLE);

        Customer customer = customerRepository.findById(customerId).get();

        assertThat(customer.getAuthenticationType()).isEqualTo(AuthenticationType.GOOGLE);
    }
}
