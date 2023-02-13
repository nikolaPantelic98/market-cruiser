package com.marketcruiser.admin.settings;

import com.marketcruiser.common.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, Long> {
}
