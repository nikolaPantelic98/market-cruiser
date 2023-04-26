package com.marketcruiser.common.entity.settings;

/**
 * The SettingsCategory enumeration represents the category of a setting.
 * It contains the following categories:
 *   GENERAL: settings that are general in nature and not specific to any category,
 *   MAIL_SERVER: settings related to the mail server configuration,
 *   MAIL_TEMPLATES: settings related to the mail templates used in the application,
 *   CURRENCY: settings related to currency configuration,
 *   PAYMENT: settings related to payment gateway configuration
 */
public enum SettingsCategory {
    GENERAL, MAIL_SERVER, MAIL_TEMPLATES, CURRENCY, PAYMENT
}
