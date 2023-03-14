package com.marketcruiser.admin.settings;

import com.marketcruiser.common.entity.settings.Settings;
import com.marketcruiser.common.entity.settings.SettingsCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class SettingsRepositoryTest {

    @Autowired
    private SettingsRepository settingsRepository;


    @Test
    public void testCreateGeneralSettings() {
        Settings siteName = new Settings("SITE_NAME", "MarketCruiser", SettingsCategory.GENERAL);
        Settings siteLogo = new Settings("SITE_LOGO", "MarketCruiser.png", SettingsCategory.GENERAL);
        Settings copyright = new Settings("COPYRIGHT", "Copyright (C) 2023 MarketCruiser Ltd.", SettingsCategory.GENERAL);
        Settings currencyId = new Settings("CURRENCY_ID", "1", SettingsCategory.CURRENCY);
        Settings symbol = new Settings("CURRENCY_SYMBOL", "$", SettingsCategory.CURRENCY);
        Settings symbolPosition = new Settings("CURRENCY_SYMBOL_POSITION", "before", SettingsCategory.CURRENCY);
        Settings decimalPointType = new Settings("DECIMAL_POINT_TYPE", "POINT", SettingsCategory.CURRENCY);
        Settings decimalDigits = new Settings("DECIMAL_DIGITS", "2", SettingsCategory.CURRENCY);
        Settings thousandsPointType = new Settings("THOUSANDS_POINT_TYPE", "COMMA", SettingsCategory.CURRENCY);

        settingsRepository.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType,
                decimalDigits, thousandsPointType));
    }

    @Test
    public void testListSettingsByCategory() {
        List<Settings> settings = settingsRepository.findByCategory(SettingsCategory.GENERAL);

        settings.forEach(System.out :: println);
    }
}
