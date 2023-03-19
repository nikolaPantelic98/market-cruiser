package com.marketcruiser.settings;

import com.marketcruiser.common.entity.settings.Settings;

import java.util.List;

public interface SettingsService {

    List<Settings> getGeneralSettings();
    EmailSettingsBag getEmailSettings();
    CurrencySettingsBag getCurrencySettings();
    PaymentSettingsBag getPaymentSettings();
    String getCurrencyCode();
}
