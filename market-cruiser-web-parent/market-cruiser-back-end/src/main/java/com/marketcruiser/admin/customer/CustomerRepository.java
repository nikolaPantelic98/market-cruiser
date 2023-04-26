package com.marketcruiser.admin.customer;

import com.marketcruiser.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * The CustomerRepository interface defines the methods to interact with the {@link Customer} entity in the database.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Finds all customers in the database whose email, first name, last name,
     * address lines 1 and 2, city, state, postal code, or country name match
     * the given keyword, and returns them in a pageable format.
     *
     * @param keyword The keyword to search for in customer fields.
     * @param pageable The criteria to sort and page the customers by.
     * @return A pageable list of customers whose fields match the given keyword.
     */
    @Query("SELECT c FROM Customer c WHERE CONCAT(c.email, ' ', c.firstName, ' ', c.lastName, ' ', "
            + "c.addressLine1, ' ', c.addressLine2, ' ', c.city, ' ', c.state, "
            + "' ', c.postCode, ' ', c.country.name) LIKE %?1%")
    Page<Customer> findAllCustomers(String keyword, Pageable pageable);

    /**
     * Updates the enabled status of the customer with the given customer ID.
     *
     * @param customerId The ID of the customer to update.
     * @param enabled The new enabled status of the customer.
     */
    @Query("UPDATE Customer c SET c.enabled = ?2 WHERE c.customerId = ?1")
    @Modifying
    void updateCustomerEnabledStatus(Long customerId, boolean enabled);

    /**
     * Finds a customer in the database with the given email.
     *
     * @param email The email of the customer to find.
     * @return The customer with the given email, or null if no such customer exists.
     */
    @Query("SELECT c FROM Customer c WHERE c.email = ?1")
    Customer findCustomerByEmail(String email);

    /**
     * Counts the number of customers in the database with the given customer ID.
     *
     * @param customerId The customer ID to count.
     * @return The number of customers with the given ID.
     */
    Long countByCustomerId(Long customerId);
}
