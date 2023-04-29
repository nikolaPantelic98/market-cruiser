package com.marketcruiser.admin.settings.country;

import com.marketcruiser.common.entity.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This class represents the REST controller for handling country-related requests.
 */
@RestController
public class CountryRestController {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryRestController(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }


    /**
     * Retrieves a list of all countries, sorted by name.
     *
     * @return A list of countries.
     */
    @GetMapping("/countries/list")
    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    /**
     * Saves a new country or updates an existing one.
     *
     * @param country The country to save or update.
     * @return The id of the saved country.
     */
    @PostMapping("/countries/save")
    public String saveCountry(@RequestBody Country country) {
        Country savedCountry = countryRepository.save(country);
        return String.valueOf(savedCountry.getCountryId());
    }

    /**
     * Deletes a country with the specified id.
     *
     * @param countryId The id of the country to delete.
     */
    @DeleteMapping("/countries/delete/{countryId}")
    public void deleteCountry(@PathVariable Long countryId) {
        countryRepository.deleteById(countryId);
    }
}
