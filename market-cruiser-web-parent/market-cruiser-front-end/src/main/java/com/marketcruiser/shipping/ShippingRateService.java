package com.marketcruiser.shipping;

import com.marketcruiser.common.entity.Address;
import com.marketcruiser.common.entity.Customer;
import com.marketcruiser.common.entity.ShippingRate;

public interface ShippingRateService {

    ShippingRate getShippingRateForCustomer(Customer customer);
    ShippingRate getShippingRateForAddress(Address address);
}
