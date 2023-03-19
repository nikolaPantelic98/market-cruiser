package com.marketcruiser.checkout;

import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
public class CheckoutInfo {

    private float productCost;
    private float productTotal;
    private float shippingCostTotal;
    private float paymentTotal;
    private int deliverDays;
    private Date deliverDate;
    private boolean codSupported;


    // calculates the expected delivery date of the order
    public Date getDeliverDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, deliverDays);

        return calendar.getTime();
    }

    // formats the payment total as a string for use in a PayPal checkout button
    public String getPaymentTotalForPayPal() {
        DecimalFormat formatter = new DecimalFormat("0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);

        return formatter.format(paymentTotal);
    }
}
