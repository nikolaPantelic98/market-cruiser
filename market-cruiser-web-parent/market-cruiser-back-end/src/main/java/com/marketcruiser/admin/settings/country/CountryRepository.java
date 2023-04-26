package com.marketcruiser.admin.settings.country;

import com.marketcruiser.common.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The CountryRepository interface defines the methods to interact with the {@link Country} entity in the database.
 */
public interface CountryRepository extends JpaRepository<Country, Long> {

    /**
     * This method returns a list of all countries in the database,
     * sorted by name in ascending order.
     *
     * @return a list of all countries in the database
     */
    List<Country> findAllByOrderByNameAsc();
}
