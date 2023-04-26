package com.marketcruiser.address;

import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * This class represents the service layer for providing the business logic related to address.
 */
@Service
@Transactional
public class AddressServiceImpl implements AddressService{

    private final AddressRepository addressRepository;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }


    /**
     * Returns a list of addresses associated with the given customer.
     *
     * @param customer the customer for which the addresses are requested.
     * @return a list of addresses associated with the given customer.
     */
    @Override
    public List<Address> listAddressBook(Customer customer) {
        return addressRepository.findAddressByCustomer(customer);
    }

    /**
     * Saves the given address to the database.
     *
     * @param address the address to be saved.
     */
    @Override
    public void saveAddress(Address address) {
        addressRepository.save(address);
    }

    /**
     * Returns the address associated with the given customer.
     *
     * @param addressId the ID of the address to be returned.
     * @param customerId the ID of the customer associated with the address.
     * @return the address associated with the given customer.
     */
    @Override
    public Address getAddress(Long addressId, Long customerId) {
        return addressRepository.findAddressByAddressIdAndCustomer(addressId, customerId);
    }

    /**
     * Deletes the address associated with the given customer.
     *
     * @param addressId the ID of the address to be deleted.
     * @param customerId the ID of the customer associated with the address.
     */
    @Override
    public void deleteAddress(Long addressId, Long customerId) {
        addressRepository.deleteAddressByAddressIdAndCustomer(addressId, customerId);
    }

    /**
     * Sets the default address for a given customer and sets all other addresses as non-default.
     *
     * @param defaultAddressId The ID of the address to be set as default.
     * @param customerId The ID of the customer whose addresses are to be updated.
     */
    @Override
    public void setDefaultAddress(Long defaultAddressId, Long customerId) {
        if (defaultAddressId > 0) {
            addressRepository.setDefaultAddress(defaultAddressId);
        }

        addressRepository.setNonDefaultForOthers(defaultAddressId, customerId);
    }

    /**
     * Retrieves the default address associated with the given customer.
     *
     * @param customer The customer whose default address is to be retrieved.
     * @return The default address associated with the given customer.
     */
    @Override
    public Address getDefaultAddress(Customer customer) {
        return addressRepository.findDefaultAddressByCustomer(customer.getCustomerId());
    }
}
