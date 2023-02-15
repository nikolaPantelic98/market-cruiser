package com.marketcruiser.admin.settings.state;

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
    @GetMapping("/states/list_by_country/{countryId}")
    public List<StateDTO> listStatesByCountry(@PathVariable Long countryId) {
        List<State> listStates = stateRepository.findByCountryOrderByNameAsc(new Country(countryId));
        List<StateDTO> result = new ArrayList<>();

        for (State state : listStates) {
            result.add(new StateDTO(state.getStateId(), state.getName()));
        }

        return result;
    }

    // method that saves state
    @PostMapping("/states/save")
    public String saveState(@RequestBody State state) {
        State savedState = stateRepository.save(state);
        return String.valueOf(savedState.getStateId());
    }

    // method that deletes state
    @DeleteMapping("/states/delete/{stateId}")
    public void deleteState(@PathVariable Long stateId) {
        stateRepository.deleteById(stateId);
    }
}
