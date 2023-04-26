package com.marketcruiser.common.entity.settings;

import java.util.List;

/**
 * The SettingsBag class is a container for a list of {@link Settings} objects.
 * It provides methods for retrieving, updating, and listing settings.
 */
public class SettingsBag {

    private List<Settings> listSettings;


    public SettingsBag(List<Settings> listSettings) {
        this.listSettings = listSettings;
    }

    /**
     * Retrieves the {@link Settings} object with the specified key.
     *
     * @param key the key of the setting to retrieve
     * @return the Settings object with the specified key, or null if it does not exist
     */
    public Settings get(String key) {
        int index = listSettings.indexOf(new Settings(key));
        if (index >= 0) {
            return listSettings.get(index);
        }

        return null;
    }

    /**
     * Retrieves the value of the setting with the specified key.
     *
     * @param key the key of the setting to retrieve
     * @return the value of the setting with the specified key, or null if it does not exist
     */
    public String getValue(String key) {
        Settings settings = get(key);
        if (settings != null) {
            return settings.getValue();
        }

        return null;
    }

    /**
     * Updates the value of the setting with the specified key.
     *
     * @param key   the key of the setting to update
     * @param value the new value of the setting
     */
    public void update(String key, String value) {
        Settings settings = get(key);
        if (settings != null && value != null) {
            settings.setValue(value);
        }
    }

    /**
     * Returns the list of settings contained in the bag.
     *
     * @return the list of settings contained in the bag
     */
    public List<Settings> list() {
        return listSettings;
    }
}
