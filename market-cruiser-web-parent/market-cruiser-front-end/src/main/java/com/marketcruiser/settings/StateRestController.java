package com.marketcruiser.settings;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.State;
import com.marketcruiser.common.entity.StateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StateRestController {

    private final StateRepository stateRepository;

    @Autowired
    public StateRestController(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }


    // retrieves a list of states for the specified country ID and returns it as a list of StateDTO objects
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
