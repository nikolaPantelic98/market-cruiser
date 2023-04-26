package com.marketcruiser.settings;

import com.marketcruiser.common.entity.settings.Settings;
import com.marketcruiser.common.entity.settings.SettingsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * The SettingsRepository interface defines the methods to interact with the {@link Settings} entity in the database.
 */
public interface SettingsRepository extends JpaRepository<Settings, String> {

    /**
     * Returns a list of all settings within the specified category.
     *
     * @param category the category to retrieve settings from
     * @return a list of settings within the specified category
     */
    List<Settings> findByCategory(SettingsCategory category);

    /**
     * Returns a list of all settings within two specified categories.
     *
     * @param categoryOne the first category to retrieve settings from
     * @param categoryTwo the second category to retrieve settings from
     * @return a list of settings within the specified categories
     */
    @Query("SELECT s FROM Settings s WHERE s.category = ?1 OR s.category = ?2")
    List<Settings> findByTwoCategories(SettingsCategory categoryOne, SettingsCategory categoryTwo);

    /**
     * Returns the setting with the specified key.
     *
     * @param key the key of the setting to retrieve
     * @return the setting with the specified key
     */
    Settings findByKey(String key);
}
