package com.marketcruiser.shipping;

import com.marketcruiser.common.entity.Country;
import com.marketcruiser.common.entity.ShippingRate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShippingRateRepositoryTest {

    @Autowired
    private ShippingRateRepository shippingRateRepository;

    @Test
    public void testFindShippingRateByCountryAndState() {
        Country usa = new Country(234L);
        String state = "New York";
        ShippingRate shippingRate = shippingRateRepository.findShippingRateByCountryAndState(usa, state);

        assertThat(shippingRate).isNotNull();
        System.out.println(shippingRate);
    }
}
