package com.marketcruiser.admin.settings;

import com.marketcruiser.common.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    List<Currency> findAllByOrderByNameAsc();
}
