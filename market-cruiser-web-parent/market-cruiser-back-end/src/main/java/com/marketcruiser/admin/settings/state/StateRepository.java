package com.marketcruiser.admin.settings.state;

import com.marketcruiser.common.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, Long> {
}
