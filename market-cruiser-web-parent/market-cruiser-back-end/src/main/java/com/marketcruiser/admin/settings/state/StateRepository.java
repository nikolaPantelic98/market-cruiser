package com.marketcruiser.admin.settings.state;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StateRepository extends JpaRepository<State, Long> {

    List<State> findByCountryOrderByNameAsc(Country country);
}
