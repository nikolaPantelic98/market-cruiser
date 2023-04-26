package com.marketcruiser.settings;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * The CountryRepository interface defines the methods to interact with the {@link Country} entity in the database.
 */
public interface CountryRepository extends JpaRepository<Country, Long> {

    /**
     * Retrieves a list of all countries ordered by name in ascending order.
     *
     * @return a list of all countries
     */
    List<Country> findAllByOrderByNameAsc();

    /**
     * Retrieves a country by its code.
     *
     * @param code the country code
     * @return the country object matching the code, null if not found
     */
    @Query("SELECT c FROM Country c WHERE c.code = ?1")
    Country findCountryByCode(String code);
}
