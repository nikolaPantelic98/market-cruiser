package com.marketcruiser.admin.settings;

import com.marketcruiser.common.entity.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository currencyRepository;


    @Test
    public void testCreateCurrencies() {
        List<Currency> listCurrencies = Arrays.asList(
                new Currency("United States Dollar", "$", "USD"),
                new Currency("British Pound", "£", "GBP"),
                new Currency("Euro", "€", "EUR"),
                new Currency("Australian Dollar", "AU$", "AUD"),
                new Currency("Canadian Dollar", "C$", "CAD")
        );

        currencyRepository.saveAll(listCurrencies);

        Iterable<Currency> iterable = currencyRepository.findAll();

        assertThat(iterable).size().isEqualTo(5);
    }
}
