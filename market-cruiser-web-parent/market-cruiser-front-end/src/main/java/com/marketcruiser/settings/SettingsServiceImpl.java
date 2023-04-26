package com.marketcruiser.settings;

import com.marketcruiser.common.entity.Currency;
import com.marketcruiser.common.entity.settings.Settings;
import com.marketcruiser.common.entity.settings.SettingsCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class represents the service layer for providing the business logic related to settings.
 */
@Service
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRepository settingsRepository;
    private final CurrencyRepository currencyRepository;

    @Autowired
    public SettingsServiceImpl(SettingsRepository settingsRepository, CurrencyRepository currencyRepository) {
        this.settingsRepository = settingsRepository;
        this.currencyRepository = currencyRepository;
    }


    /**
     * Retrieves general settings
     *
     * @return a list of Settings objects related to general settings
     */
    @Override
    public List<Settings> getGeneralSettings() {
        return settingsRepository.findByTwoCategories(SettingsCategory.GENERAL, SettingsCategory.CURRENCY);
    }

    /**
     * Retrieves email settings
     *
     * @return an EmailSettingsBag object containing email settings
     */
    @Override
    public EmailSettingsBag getEmailSettings() {
        List<Settings> settings = settingsRepository.findByCategory(SettingsCategory.MAIL_SERVER);
        settings.addAll(settingsRepository.findByCategory(SettingsCategory.MAIL_TEMPLATES));

        return new EmailSettingsBag(settings);
    }

    /**
     * Retrieves currency settings
     *
     * @return a CurrencySettingsBag object containing currency settings
     */
    @Override
    public CurrencySettingsBag getCurrencySettings() {
        List<Settings> settings = settingsRepository.findByCategory(SettingsCategory.CURRENCY);

        return new CurrencySettingsBag(settings);
    }

    /**
     * Retrieves payment settings
     *
     * @return a PaymentSettingsBag object containing payment settings
     */
    @Override
    public PaymentSettingsBag getPaymentSettings() {
        List<Settings> settings = settingsRepository.findByCategory(SettingsCategory.PAYMENT);

        return new PaymentSettingsBag(settings);
    }

    /**
     * Retrieves the currency code for a specific currency
     *
     * @return a string representing the currency code
     */
    @Override
    public String getCurrencyCode() {
        Settings setting = settingsRepository.findByKey("CURRENCY_ID");
        Long currencyId = Long.parseLong(setting.getValue());
        Currency currency = currencyRepository.findById(currencyId).get();

        return currency.getCode();
    }
}