package com.marketcruiser.admin.settings;

import com.marketcruiser.common.entity.Settings;
import com.marketcruiser.common.entity.SettingsCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingsServiceImpl implements SettingsService{

    private final SettingsRepository settingsRepository;

    @Autowired
    public SettingsServiceImpl(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }


    @Override
    public List<Settings> listAllSettings() {
        return settingsRepository.findAll();
    }

    // retrieves the general settings and currency settings from the settings repository
    @Override
    public GeneralSettingsBag getGeneralSettings() {
        List<Settings> settings = new ArrayList<>();

        List<Settings> generalSettings = settingsRepository.findByCategory(SettingsCategory.GENERAL);
        List<Settings> currencySettings = settingsRepository.findByCategory(SettingsCategory.CURRENCY);

        settings.addAll(generalSettings);
        settings.addAll(currencySettings);

        return new GeneralSettingsBag(settings);
    }

    // method that saves all the given settings to the settings repository
    @Override
    public void saveAllSettings(Iterable<Settings> settings) {
        settingsRepository.saveAll(settings);
    }


}
