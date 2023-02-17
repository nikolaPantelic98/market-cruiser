package com.marketcruiser.settings;

import com.marketcruiser.common.entity.Settings;

import java.util.List;

public interface SettingsService {

    List<Settings> getGeneralSettings();
    EmailSettingsBag getEmailSettings();
}
