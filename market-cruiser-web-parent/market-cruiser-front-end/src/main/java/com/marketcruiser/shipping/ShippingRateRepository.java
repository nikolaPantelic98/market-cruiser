package com.marketcruiser.shipping;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingRateRepository extends JpaRepository<ShippingRate, Long> {

    ShippingRate findShippingRateByCountryAndState(Country country, String state);
}
