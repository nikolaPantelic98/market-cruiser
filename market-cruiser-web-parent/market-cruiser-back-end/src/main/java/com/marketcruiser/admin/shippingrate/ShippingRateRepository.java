package com.marketcruiser.admin.shippingrate;

import com.marketcruiser.common.entity.ShippingRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * The ShippingRateRepository interface defines the methods to interact with the {@link ShippingRate} entity in the database.
 */
public interface ShippingRateRepository extends JpaRepository<ShippingRate, Long> {

    /**
     * This method finds a shipping rate entity that matches the given country and state.
     *
     * @param countryId the ID of the country to search for
     * @param state the state to search for
     * @return the ShippingRate entity matching the given country and state, if found
     */
    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.countryId = ?1 AND sr.state = ?2")
    ShippingRate findByCountryAndState(Long countryId, String state);

    /**
     * This method updates the COD support status of a shipping rate entity.
     *
     * @param shippingRateId the ID of the shipping rate to update
     * @param enabled the new value for the COD support status
     */
    @Query("UPDATE ShippingRate sr SET sr.codSupported = ?2 WHERE sr.shippingRateId = ?1")
    @Modifying
    void updateCODSupport(Long shippingRateId, boolean enabled);

    /**
     * This method returns a page of shipping rate entities that match the given keyword.
     * The search is performed on the name of the country and the name of the state.
     *
     * @param keyword the keyword to search for
     * @param pageable the Pageable object containing pagination information
     * @return a page of ShippingRate entities matching the given keyword
     */
    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.name LIKE %?1% "
            + "OR sr.state LIKE %?1% ")
    Page<ShippingRate> findAllShippingRates(String keyword, Pageable pageable);

    /**
     * This method counts the number of shipping rate entities with the given ID.
     *
     * @param shippingRateId the ID of the shipping rate to count
     * @return the number of shipping rate entities with the given ID
     */
    Long countByShippingRateId(Long shippingRateId);
}
