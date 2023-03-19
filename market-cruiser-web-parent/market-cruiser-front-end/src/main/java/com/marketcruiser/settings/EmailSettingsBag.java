package com.marketcruiser.settings;

import com.marketcruiser.common.entity.settings.Settings;
import com.marketcruiser.common.entity.settings.SettingsBag;

import java.util.List;

public class EmailSettingsBag extends SettingsBag {

    public EmailSettingsBag(List<Settings> listSettings) {
        super(listSettings);
    }

    public String getHost() {
        return super.getValue("MAIL_HOST");
    }

    public int getPort() {
        return Integer.parseInt(super.getValue("MAIL_PORT"));
    }

    public String getUsername() {
        return super.getValue("MAIL_USERNAME");
    }

    public String getPassword() {
        return super.getValue("MAIL_PASSWORD");
    }

    public String getSmtpAuth() {
        return super.getValue("SMTP_AUTH");
    }

    public String getSmtpSecured() {
        return super.getValue("SMTP_SECURED");
    }

    public String getFromAddress() {
        return super.getValue("MAIL_FROM");
    }

    public String getSenderName() {
        return super.getValue("MAIL_SENDER");
    }

    public String getCustomerVerifySubject() {
        return super.getValue("CUSTOMER_VERIFY_SUBJECT");
    }

    public String getCustomerVerifyContent() {
        return super.getValue("CUSTOMER_VERIFY_CONTENT");
    }

    public String getOrderConfirmationSubject() {
        return super.getValue("ORDER_CONFIRMATION_SUBJECT");
    }

    public String getOrderConfirmationContent() {
        return super.getValue("ORDER_CONFIRMATION_CONTENT");
    }
}
