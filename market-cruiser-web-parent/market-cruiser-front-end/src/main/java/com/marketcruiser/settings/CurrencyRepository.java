package com.marketcruiser.settings;

import com.marketcruiser.common.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The CurrencyRepository interface defines the methods to interact with the {@link Currency} entity in the database.
 */
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
}
