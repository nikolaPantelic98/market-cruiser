package com.marketcruiser.admin.shippingrate;

import com.marketcruiser.common.entity.ShippingRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ShippingRateRepository extends JpaRepository<ShippingRate, Long> {

    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.countryId = ?1 AND sr.state = ?2")
    ShippingRate findByCountryAndState(Long countryId, String state);

    @Query("UPDATE ShippingRate sr SET sr.codSupported = ?2 WHERE sr.shippingRateId = ?1")
    @Modifying
    void updateCODSupport(Long shippingRateId, boolean enabled);

    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.name LIKE %?1% "
            + "OR sr.state LIKE %?1% ")
    Page<ShippingRate> findAllShippingRates(String keyword, Pageable pageable);

    Long countByShippingRateId(Long shippingRateId);
}
