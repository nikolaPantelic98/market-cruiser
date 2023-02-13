package com.marketcruiser.admin.settings.country;

import com.marketcruiser.common.entity.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CountryRestController {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryRestController(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }


    @GetMapping("/countries/list")
    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    @PostMapping("/countries/save")
    public String saveCountry(@RequestBody Country country) {
        Country savedCountry = countryRepository.save(country);
        return String.valueOf(savedCountry.getCountryId());
    }

    @GetMapping("/countries/delete/{countryId}")
    public void deleteCountry(@PathVariable Long countryId) {
        countryRepository.deleteById(countryId);
    }
}
