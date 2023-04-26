package com.marketcruiser.admin.settings;

import com.marketcruiser.common.entity.settings.Settings;
import com.marketcruiser.common.entity.settings.SettingsCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The SettingsRepository interface defines the methods to interact with the {@link Settings} entity in the database.
 */
public interface SettingsRepository extends JpaRepository<Settings, String> {

    /**
     * Finds all Settings by category.
     *
     * @param category the category of the settings to retrieve
     * @return a list of settings matching the specified category
     */
    List<Settings> findByCategory(SettingsCategory category);
}
