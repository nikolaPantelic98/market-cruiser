package com.marketcruiser.address;

import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.Customer;

import java.util.List;

public interface AddressService {

    List<Address> listAddressBook(Customer customer);
}
