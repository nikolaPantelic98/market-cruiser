package com.marketcruiser.settings;

import com.marketcruiser.common.entity.settings.Settings;
import com.marketcruiser.common.entity.settings.SettingsBag;

import java.util.List;

/**
 * A class for holding payment settings data as a collection of key-value pairs.
 * Extends the {@link SettingsBag} class.
 */
public class PaymentSettingsBag extends SettingsBag {

    public PaymentSettingsBag(List<Settings> listSettings) {
        super(listSettings);
    }


    public String getUrl() {
        return super.getValue("PAYPAL_API_BASE_URL");
    }

    public String getClientId() {
        return super.getValue("PAYPAL_API_CLIENT_ID");
    }

    public String getClientSecret() {
        return super.getValue("PAYPAL_API_CLIENT_SECRET");
    }
}
