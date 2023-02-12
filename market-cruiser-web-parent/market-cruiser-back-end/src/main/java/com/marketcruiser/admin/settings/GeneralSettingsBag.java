package com.marketcruiser.admin.settings;

import com.marketcruiser.common.entity.Settings;
import com.marketcruiser.common.entity.SettingsBag;

import java.util.List;

public class GeneralSettingsBag extends SettingsBag {

    public GeneralSettingsBag(List<Settings> listSettings) {
        super(listSettings);
    }

    public void updateCurrencySymbol(String value) {
        super.update("CURRENCY_SYMBOL", value);
    }

    public void updateSiteLogo(String value) {
        super.update("SITE_LOGO", value);
    }
}
