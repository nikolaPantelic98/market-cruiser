package com.marketcruiser.address;

import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * The AddressRepository interface defines the methods to interact with the {@link Address} entity in the database.
 */
public interface AddressRepository extends JpaRepository<Address, Long> {

    /**
     * Finds all the addresses for a specific customer.
     * @param customer the customer entity to look up.
     * @return a list of addresses for the specified customer.
     */
    List<Address> findAddressByCustomer(Customer customer);

    /**
     * Finds an address with a specific id and customer id.
     * @param addressId the id of the address to look up.
     * @param customerId the id of the customer associated with the address.
     * @return the address entity matching the provided addressId and customerId.
     */
    @Query("SELECT a FROM Address a WHERE a.addressId = ?1 AND a.customer.customerId = ?2")
    Address findAddressByAddressIdAndCustomer(Long addressId, Long customerId);

    /**
     * Deletes an address with a specific id and customer id.
     * @param addressId the id of the address to delete.
     * @param customerId the id of the customer associated with the address.
     */
    @Query("DELETE FROM Address a WHERE a.addressId = ?1 AND a.customer.customerId = ?2")
    @Modifying
    void deleteAddressByAddressIdAndCustomer(Long addressId, Long customerId);

    /**
     * Sets an address as the default address for a specific customer.
     * @param addressId the id of the address to set as default.
     */
    @Query("UPDATE Address a SET a.defaultForShipping = true WHERE a.addressId = ?1")
    @Modifying
    void setDefaultAddress(Long addressId);

    /**
     * Sets all other addresses for a customer as non-default.
     * @param defaultAddressId the id of the default address.
     * @param customerId the id of the customer associated with the addresses.
     */
    @Query("UPDATE Address a SET a.defaultForShipping = false WHERE a.addressId != ?1 AND a.customer.customerId = ?2")
    @Modifying
    void setNonDefaultForOthers(Long defaultAddressId, Long customerId);

    /**
     * Finds the default address for a specific customer.
     * @param customerId the id of the customer to look up.
     * @return the address entity for the default address of the specified customer.
     */
    @Query("SELECT a FROM Address a WHERE a.customer.customerId = ?1 AND a.defaultForShipping = true")
    Address findDefaultAddressByCustomer(Long customerId);
}
