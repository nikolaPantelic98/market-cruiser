package com.marketcruiser.settings;

import com.marketcruiser.common.entity.Settings;
import com.marketcruiser.common.entity.SettingsCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository settingsRepository;

    @Autowired
    public SettingsServiceImpl(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }


    @Override
    public List<Settings> getGeneralSettings() {
        return settingsRepository.findByTwoCategories(SettingsCategory.GENERAL, SettingsCategory.CURRENCY);
    }

    @Override
    public EmailSettingsBag getEmailSettings() {
        List<Settings> settings = settingsRepository.findByCategory(SettingsCategory.MAIL_SERVER);
        settings.addAll(settingsRepository.findByCategory(SettingsCategory.MAIL_TEMPLATES));

        return new EmailSettingsBag(settings);
    }

}
