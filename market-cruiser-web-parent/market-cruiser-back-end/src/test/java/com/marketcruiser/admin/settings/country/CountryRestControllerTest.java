package com.marketcruiser.admin.settings.country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketcruiser.common.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    CountryRepository countryRepository;


    @Test
    @WithMockUser(username = "admin@gmail.com", password = "admin123", roles = "Admin")
    public void testListCountries() throws Exception {
        String url = "/countries/list";
        MvcResult result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);

        assertThat(countries).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", password = "admin123", roles = "Admin")
    public void testCreateCountry() throws Exception {
        String url = "/countries/save";
        String countryName = "United Kingdom";
        String countryCode = "UK";
        Country country = new Country(countryName, countryCode);

        MvcResult result = mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Long countryId = Long.parseLong(response);

        Optional<Country> findById = countryRepository.findById(countryId);
        assertThat(findById).isPresent();

        Country savedCountry = findById.get();

        assertThat(savedCountry.getName()).isEqualTo(countryName);
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", password = "admin123", roles = "Admin")
    public void testUpdateCountry() throws Exception {
        String url = "/countries/save";

        Long countryId = 5L;
        String countryName = "Mexico";
        String countryCode = "MEX";
        Country country = new Country(countryId, countryName, countryCode);

        mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(countryId)));

        Optional<Country> findById = countryRepository.findById(countryId);
        assertThat(findById).isPresent();

        Country savedCountry = findById.get();

        assertThat(savedCountry.getName()).isEqualTo(countryName);
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", password = "admin123", roles = "Admin")
    public void testDeleteCountry() throws Exception {
        Long countryId = 10L;
        String url = "/countries/delete/" + countryId;
        mockMvc.perform(get(url)).andExpect(status().isOk());

        Optional<Country> findById = countryRepository.findById(countryId);

        assertThat(findById).isNotPresent();
    }
}
