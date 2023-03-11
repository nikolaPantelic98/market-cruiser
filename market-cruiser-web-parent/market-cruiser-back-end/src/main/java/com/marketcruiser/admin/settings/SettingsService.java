package com.marketcruiser.admin.settings;

import com.marketcruiser.common.entity.Settings;

import java.util.List;

public interface SettingsService {

    List<Settings> listAllSettings();
    GeneralSettingsBag getGeneralSettings();
    void saveAllSettings(Iterable<Settings> settings);
    List<Settings> getMailServerSettings();
    List<Settings> getMailTemplateSettings();
    List<Settings> getCurrencySettings();
}
