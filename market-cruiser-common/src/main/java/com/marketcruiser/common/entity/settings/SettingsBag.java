package com.marketcruiser.common.entity.settings;

import com.marketcruiser.common.entity.settings.Settings;

import java.util.List;

public class SettingsBag {

    private List<Settings> listSettings;


    public SettingsBag(List<Settings> listSettings) {
        this.listSettings = listSettings;
    }

    public Settings get(String key) {
        int index = listSettings.indexOf(new Settings(key));
        if (index >= 0) {
            return listSettings.get(index);
        }

        return null;
    }

    public String getValue(String key) {
        Settings settings = get(key);
        if (settings != null) {
            return settings.getValue();
        }

        return null;
    }

    public void update(String key, String value) {
        Settings settings = get(key);
        if (settings != null && value != null) {
            settings.setValue(value);
        }
    }

    public List<Settings> list() {
        return listSettings;
    }
}
