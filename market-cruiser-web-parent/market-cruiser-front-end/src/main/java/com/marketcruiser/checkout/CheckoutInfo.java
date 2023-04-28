package com.marketcruiser.checkout;

import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;

/**
 * The CheckoutInfo class represents the checkout information for an order and provides methods to calculate
 * the expected delivery date and format the payment total for use in a PayPal checkout button.
 */
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


    /**
     * Calculates the expected delivery date of the order based on the current date and the
     * specified number of delivery days.
     *
     * @return The expected delivery date of the order.
     */

    public Date getDeliverDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, deliverDays);

        return calendar.getTime();
    }

    /**
     * Formats the payment total as a string with two decimal places using a period as the decimal
     * separator for use in a PayPal checkout button.
     *
     * @return The payment total formatted as a string for use in a PayPal checkout button.
     */
    public String getPaymentTotalForPayPal() {
        DecimalFormat formatter = new DecimalFormat("0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        formatter.setDecimalFormatSymbols(symbols);

        return formatter.format(paymentTotal);
    }
}
