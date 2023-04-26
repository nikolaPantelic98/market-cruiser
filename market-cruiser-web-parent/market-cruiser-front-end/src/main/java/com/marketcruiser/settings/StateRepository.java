package com.marketcruiser.settings;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The StateRepository interface defines the methods to interact with the {@link State} entity in the database.
 */
public interface StateRepository extends JpaRepository<State, Long> {

    /**
     * Find all states for a given country and order them by name in ascending order.
     *
     * @param country the country for which to retrieve the states
     * @return a list of states for the given country ordered by name in ascending order
     */
    List<State> findByCountryOrderByNameAsc(Country country);
}
