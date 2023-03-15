package com.marketcruiser.address;

import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findAddressByCustomer(Customer customer);

    @Query("SELECT a FROM Address a WHERE a.addressId = ?1 AND a.customer.customerId = ?2")
    Address findAddressByAddressIdAndCustomer(Long addressId, Long customerId);

    @Query("DELETE FROM Address a WHERE a.addressId = ?1 AND a.customer.customerId = ?2")
    @Modifying
    void deleteAddressByAddressIdAndCustomer(Long addressId, Long customerId);

    @Query("UPDATE Address a SET a.defaultForShipping = true WHERE a.addressId = ?1")
    @Modifying
    void setDefaultAddress(Long addressId);

    @Query("UPDATE Address a SET a.defaultForShipping = false WHERE a.addressId != ?1 AND a.customer.customerId = ?2")
    @Modifying
    void setNonDefaultForOthers(Long defaultAddressId, Long customerId);

    @Query("SELECT a FROM Address a WHERE a.customer.customerId = ?1 AND a.defaultForShipping = true")
    Address findDefaultAddressByCustomer(Long customerId);
}
