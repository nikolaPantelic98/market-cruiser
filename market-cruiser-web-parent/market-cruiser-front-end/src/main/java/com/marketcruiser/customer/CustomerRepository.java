package com.marketcruiser.customer;

import com.marketcruiser.common.entity.AuthenticationType;
import com.marketcruiser.common.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * The CustomerRepository interface defines the methods to interact with the {@link Customer} entity in the database.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Finds a customer by their email address.
     * @param email the email address to look up.
     * @return the customer entity matching the provided email address.
     */
    @Query("SELECT c FROM Customer c WHERE c.email = ?1")
    Customer findCustomerByEmail(String email);

    /**
     * Finds a customer by their verification code.
     * @param verificationCode the verification code to look up.
     * @return the customer entity matching the provided verification code.
     */
    @Query("SELECT c FROM Customer c WHERE c.verificationCode = ?1")
    Customer findCustomerByVerificationCode(String verificationCode);

    /**
     * Enables a customer with a specific ID by setting their enabled flag to true and their verification code to null.
     * @param customerId the ID of the customer to enable.
     */
    @Query("UPDATE Customer c SET c.enabled = true, c.verificationCode = null WHERE c.customerId = ?1")
    @Modifying
    void enableCustomer(Long customerId);

    /**
     * Updates the authentication type of a customer with a specific ID.
     * @param customerId the ID of the customer to update.
     * @param type the new authentication type to set for the customer.
     */
    @Query("UPDATE Customer c SET c.authenticationType = ?2 WHERE c.customerId = ?1")
    @Modifying
    void updateAuthenticationType(Long customerId, AuthenticationType type);

    /**
     * Finds a customer by their reset password token.
     * @param token the reset password token to look up.
     * @return the customer entity matching the provided reset password token.
     */
    Customer findByResetPasswordToken(String token);
}
