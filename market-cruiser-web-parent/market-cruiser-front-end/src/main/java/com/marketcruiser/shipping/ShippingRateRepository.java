package com.marketcruiser.shipping;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The ShippingRateRepository interface defines the methods to interact with the {@link ShippingRate} entity in the database.
 */
public interface ShippingRateRepository extends JpaRepository<ShippingRate, Long> {

    /**
     * Retrieves the ShippingRate for the given country and state.
     *
     * @param country The country to search for
     * @param state The state to search for
     * @return The ShippingRate for the given country and state, or null if not found
     */
    ShippingRate findShippingRateByCountryAndState(Country country, String state);
}
