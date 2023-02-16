package com.marketcruiser.customer;

import com.marketcruiser.common.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.email = ?1")
    Customer findCustomerByEmail(String email);
    @Query("SELECT c FROM Customer c WHERE c.verificationCode = ?1")
    Customer findCustomerByVerificationCode(String verificationCode);
    @Query("UPDATE Customer c SET c.enabled = true WHERE c.customerId = ?1")
    @Modifying
    void enableCustomer(Long customerId);
}
