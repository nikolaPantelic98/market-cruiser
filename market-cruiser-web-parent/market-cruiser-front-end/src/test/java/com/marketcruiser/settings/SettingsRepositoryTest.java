package com.marketcruiser.settings;

import com.marketcruiser.common.entity.settings.Settings;
import com.marketcruiser.common.entity.settings.SettingsCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class SettingsRepositoryTest {

    @Autowired
    private SettingsRepository settingsRepository;


    @Test
    public void testFindByTwoCategories() {
        List<Settings> settings = settingsRepository.findByTwoCategories(SettingsCategory.GENERAL, SettingsCategory.CURRENCY);
        settings.forEach(System.out :: println);
    }
}
