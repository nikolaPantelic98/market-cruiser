package com.marketcruiser.settings;

import com.marketcruiser.common.entity.settings.Settings;
import com.marketcruiser.common.entity.settings.SettingsBag;

import java.util.List;

public class CurrencySettingsBag extends SettingsBag {

    public CurrencySettingsBag(List<Settings> listSettings) {
        super(listSettings);
    }

    public String getSymbol() {
        return super.getValue("CURRENCY_SYMBOL");
    }

    public String getSymbolPosition() {
        return super.getValue("CURRENCY_SYMBOL_POSITION");
    }

    public String getDecimalPointType() {
        return super.getValue("DECIMAL_POINT_TYPE");
    }

    public String getThousandPointType() {
        return super.getValue("THOUSANDS_POINT_TYPE");
    }

    public int getDecimalDigits() {
        return Integer.parseInt(super.getValue("DECIMAL_DIGITS"));
    }
}
