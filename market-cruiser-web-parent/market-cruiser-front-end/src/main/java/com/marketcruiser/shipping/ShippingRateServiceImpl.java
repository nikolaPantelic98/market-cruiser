package com.marketcruiser.shipping;

import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.ShippingRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingRateServiceImpl implements ShippingRateService{

    private final ShippingRateRepository shippingRateRepository;

    @Autowired
    public ShippingRateServiceImpl(ShippingRateRepository shippingRateRepository) {
        this.shippingRateRepository = shippingRateRepository;
    }


    // retrieves the shipping rate for a given customer's state or city, based on the customer's country
    @Override
    public ShippingRate getShippingRateForCustomer(Customer customer) {
        String state = customer.getState();
        if (state == null || state.isEmpty()) {
            state = customer.getCity();
        }

        return shippingRateRepository.findShippingRateByCountryAndState(customer.getCountry(), state);
    }

    // retrieves the shipping rate for a given address's state or city, based on the address's country
    @Override
    public ShippingRate getShippingRateForAddress(Address address) {
        String state = address.getState();
        if (state == null || state.isEmpty()) {
            state = address.getCity();
        }

        return shippingRateRepository.findShippingRateByCountryAndState(address.getCountry(), state);
    }
}
