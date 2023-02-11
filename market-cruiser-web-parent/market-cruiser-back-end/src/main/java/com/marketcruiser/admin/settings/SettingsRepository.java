package com.marketcruiser.admin.settings;

import com.marketcruiser.common.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Settings, String> {


}
