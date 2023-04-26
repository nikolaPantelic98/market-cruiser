package com.marketcruiser.shipping;

import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.ShippingRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class represents the service layer for providing the business logic related to shipping rate.
 */
@Service
public class ShippingRateServiceImpl implements ShippingRateService{

    private final ShippingRateRepository shippingRateRepository;

    @Autowired
    public ShippingRateServiceImpl(ShippingRateRepository shippingRateRepository) {
        this.shippingRateRepository = shippingRateRepository;
    }


    /**
     * Retrieve the shipping rate for a customer by their state and country
     *
     * @param customer The customer to get the shipping rate for
     * @return The shipping rate for the customer's location
     */
    @Override
    public ShippingRate getShippingRateForCustomer(Customer customer) {
        String state = customer.getState();
        if (state == null || state.isEmpty()) {
            state = customer.getCity();
        }

        return shippingRateRepository.findShippingRateByCountryAndState(customer.getCountry(), state);
    }

    /**
     * Retrieve the shipping rate for an address by its state and country
     *
     * @param address The address to get the shipping rate for
     * @return The shipping rate for the address's location
     */
    @Override
    public ShippingRate getShippingRateForAddress(Address address) {
        String state = address.getState();
        if (state == null || state.isEmpty()) {
            state = address.getCity();
        }

        return shippingRateRepository.findShippingRateByCountryAndState(address.getCountry(), state);
    }
}
