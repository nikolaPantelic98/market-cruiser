package com.marketcruiser.admin.settings.state;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The StateRepository interface defines the methods to interact with the {@link State} entity in the database.
 */
public interface StateRepository extends JpaRepository<State, Long> {

    /**
     * This method returns a list of states belonging to a particular country,
     * sorted by name in ascending order.
     *
     * @param country the country whose states are to be fetched
     * @return a list of states belonging to the given country
     */
    List<State> findByCountryOrderByNameAsc(Country country);
}
