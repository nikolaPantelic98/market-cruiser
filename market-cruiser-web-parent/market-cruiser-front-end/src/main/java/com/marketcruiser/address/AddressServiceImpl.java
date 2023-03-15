package com.marketcruiser.address;

import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements AddressService{

    private final AddressRepository addressRepository;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }


    // returns a list of addresses associated with the given customer
    @Override
    public List<Address> listAddressBook(Customer customer) {
        return addressRepository.findAddressByCustomer(customer);
    }

    // saves the given address to the database
    @Override
    public void saveAddress(Address address) {
        addressRepository.save(address);
    }

    // returns the address associated with the given customer
    @Override
    public Address getAddress(Long addressId, Long customerId) {
        return addressRepository.findAddressByAddressIdAndCustomer(addressId, customerId);
    }

    // deletes the address associated with the given customer
    @Override
    public void deleteAddress(Long addressId, Long customerId) {
        addressRepository.deleteAddressByAddressIdAndCustomer(addressId, customerId);
    }

    // sets the default address for a given customer, and sets all other addresses as non-default
    @Override
    public void setDefaultAddress(Long defaultAddressId, Long customerId) {
        if (defaultAddressId > 0) {
            addressRepository.setDefaultAddress(defaultAddressId);
        }

        addressRepository.setNonDefaultForOthers(defaultAddressId, customerId);
    }

    @Override
    public Address getDefaultAddress(Customer customer) {
        return addressRepository.findDefaultAddressByCustomer(customer.getCustomerId());
    }
}
