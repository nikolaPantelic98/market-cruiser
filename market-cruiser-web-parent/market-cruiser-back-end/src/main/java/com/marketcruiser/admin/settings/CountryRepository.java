package com.marketcruiser.admin.settings;

import com.marketcruiser.common.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Long> {

    List<Country> findAllByOrderByNameAsc();
}
