package com.marketcruiser.admin.settings;

import com.marketcruiser.common.entity.Settings;
import com.marketcruiser.common.entity.SettingsCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettingsRepository extends JpaRepository<Settings, String> {

    List<Settings> findByCategory(SettingsCategory category);
}
