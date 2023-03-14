package com.marketcruiser.settings;

import com.marketcruiser.common.entity.settings.Settings;
import com.marketcruiser.common.entity.settings.SettingsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SettingsRepository extends JpaRepository<Settings, String> {

    List<Settings> findByCategory(SettingsCategory category);
    @Query("SELECT s FROM Settings s WHERE s.category = ?1 OR s.category = ?2")
    List<Settings> findByTwoCategories(SettingsCategory categoryOne, SettingsCategory categoryTwo);
}
