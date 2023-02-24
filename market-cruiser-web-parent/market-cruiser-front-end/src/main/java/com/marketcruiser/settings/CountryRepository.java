package com.marketcruiser.settings;

import com.marketcruiser.common.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Long> {

    List<Country> findAllByOrderByNameAsc();
    @Query("SELECT c FROM Country c WHERE c.code = ?1")
    Country findCountryByCode(String code);
}
