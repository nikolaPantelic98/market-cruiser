package com.marketcruiser.admin.settings.country;

import com.marketcruiser.admin.settings.country.CountryRepository;
import com.marketcruiser.common.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository countryRepository;


    @Test
    public void testCreateCountry() {
        Country country = countryRepository.save(new Country("Germany", "DE"));
        assertThat(country).isNotNull();
        assertThat(country.getCountryId()).isGreaterThan(0);
    }

    @Test
    public void testListCountries() {
        List<Country> listCountries = countryRepository.findAllByOrderByNameAsc();
        listCountries.forEach(System.out :: println);

        assertThat(listCountries.size()).isGreaterThan(0);
    }

    @Test
    public void testUpdateCountry() {
        Long countryId = 1L;
        String name = "United States of America";

        Country country = countryRepository.findById(countryId).get();
        country.setName(name);

        Country updatedCountry = countryRepository.save(country);

        System.out.println("Update: " + country);

        assertThat(updatedCountry.getName()).isEqualTo(name);
    }

    @Test
    public void testGetCountry() {
        Long countryId = 1L;
        Country country = countryRepository.findById(countryId).get();

        System.out.println("Get after update: " + country);

        assertThat(country).isNotNull();
    }

    @Test
    public void testDeleteCountry() {
        Long countryId = 1L;
        countryRepository.deleteById(countryId);

        Optional<Country> findById = countryRepository.findById(countryId);
        assertThat(findById).isEmpty();
    }
}
