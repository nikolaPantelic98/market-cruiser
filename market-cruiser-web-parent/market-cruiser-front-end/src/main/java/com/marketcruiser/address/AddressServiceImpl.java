package com.marketcruiser.address;

import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    private final AddressRepository addressRepository;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }


    @Override
    public List<Address> listAddressBook(Customer customer) {
        return addressRepository.findAddressByCustomer(customer);
    }
}
