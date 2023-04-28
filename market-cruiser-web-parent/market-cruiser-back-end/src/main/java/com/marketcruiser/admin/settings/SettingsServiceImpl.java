package com.marketcruiser.admin.settings;

import com.marketcruiser.common.entity.settings.Settings;
import com.marketcruiser.common.entity.settings.SettingsCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link  SettingsService} interface that provides methods for managing settings.
 */
@Service
public class SettingsServiceImpl implements SettingsService{

    private final SettingsRepository settingsRepository;

    @Autowired
    public SettingsServiceImpl(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }


    /**
     * Returns a list of all Settings.
     */
    @Override
    public List<Settings> listAllSettings() {
        return settingsRepository.findAll();
    }

    /**
     * Retrieves the general settings and currency settings from the settings repository.
     *
     * @return a GeneralSettingsBag object containing the general and currency settings
     */
    @Override
    public GeneralSettingsBag getGeneralSettings() {
        List<Settings> settings = new ArrayList<>();

        List<Settings> generalSettings = settingsRepository.findByCategory(SettingsCategory.GENERAL);
        List<Settings> currencySettings = settingsRepository.findByCategory(SettingsCategory.CURRENCY);

        settings.addAll(generalSettings);
        settings.addAll(currencySettings);

        return new GeneralSettingsBag(settings);
    }

    /**
     * Saves all the given settings to the settings repository.
     *
     * @param settings an Iterable of Settings to save
     */
    @Override
    public void saveAllSettings(Iterable<Settings> settings) {
        settingsRepository.saveAll(settings);
    }

    /**
     * Returns a list of all mail server settings.
     */
    @Override
    public List<Settings> getMailServerSettings() {
        return settingsRepository.findByCategory(SettingsCategory.MAIL_SERVER);
    }

    /**
     * Returns a list of all mail template settings.
     */
    @Override
    public List<Settings> getMailTemplateSettings() {
        return settingsRepository.findByCategory(SettingsCategory.MAIL_TEMPLATES);
    }

    /**
     * Returns a list of all currency settings.
     */
    @Override
    public List<Settings> getCurrencySettings() {
        return settingsRepository.findByCategory(SettingsCategory.CURRENCY);
    }

    /**
     * Returns a list of all payment settings.
     */
    @Override
    public List<Settings> getPaymentSettings() {
        return settingsRepository.findByCategory(SettingsCategory.PAYMENT);
    }
}
