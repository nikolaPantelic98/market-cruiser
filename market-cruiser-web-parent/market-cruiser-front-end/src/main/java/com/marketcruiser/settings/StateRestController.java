package com.marketcruiser.settings;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.State;
import com.marketcruiser.common.entity.StateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Rest controller that provides endpoints for managing states.
 */
@RestController
public class StateRestController {

    private final StateRepository stateRepository;

    @Autowired
    public StateRestController(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }


    /**
     * Endpoint that retrieves a list of states for the specified country ID and returns it as a list of StateDTO objects
     *
     * @param countryId the ID of the country for which to retrieve the list of states
     * @return a list of StateDTO objects containing the ID and name of each state
     */
    @GetMapping("/settings/list_states_by_country/{countryId}")
    public List<StateDTO> listStatesByCountry(@PathVariable Long countryId) {
        List<State> listStates = stateRepository.findByCountryOrderByNameAsc(new Country(countryId));
        List<StateDTO> result = new ArrayList<>();

        for (State state : listStates) {
            result.add(new StateDTO(state.getStateId(), state.getName()));
        }

        return result;
    }
}
