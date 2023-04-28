package com.marketcruiser.admin.settings;

import com.marketcruiser.common.entity.settings.Settings;
import com.marketcruiser.common.entity.settings.SettingsBag;

import java.util.List;

/**
 * The GeneralSettingsBag class represents a specific settings bag for general settings.
 * It extends the base {@link SettingsBag} class.
 */
public class GeneralSettingsBag extends SettingsBag {

    public GeneralSettingsBag(List<Settings> listSettings) {
        super(listSettings);
    }

    /**
     * Updates the value of the "CURRENCY_SYMBOL" setting to the provided value.
     *
     * @param value A String representing the new value for the "CURRENCY_SYMBOL" setting.
     */
    public void updateCurrencySymbol(String value) {
        super.update("CURRENCY_SYMBOL", value);
    }

    /**
     * Updates the value of the "SITE_LOGO" setting to the provided value.
     *
     * @param value A String representing the new value for the "SITE_LOGO" setting.
     */
    public void updateSiteLogo(String value) {
        super.update("SITE_LOGO", value);
    }
}
