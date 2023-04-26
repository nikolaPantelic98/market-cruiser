package com.marketcruiser.admin.settings;

import com.marketcruiser.common.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The CurrencyRepository interface defines the methods to interact with the {@link Currency} entity in the database.
 */
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    /**
     * Returns all currencies in ascending order of name.
     *
     * @return List of currencies sorted by name in ascending order.
     */
    List<Currency> findAllByOrderByNameAsc();
}
