var fieldProductCost;
var fieldSubtotal;
var fieldShippingCost;
var fieldTax;
var fieldTotal;

/**
 * This function sets the initial values for the fields that represent the cost of the order.
 * It also formats the values in the fields using the `formatOrderAmounts()` and `formatProductAmounts()` functions.
 * These formatted values include the use of thousand separators and are rounded to two decimal places.
 */
$(document).ready(function () {
    fieldProductCost = $("#productCost");
    fieldSubtotal = $("#subtotal");
    fieldShippingCost = $("#shippingCost");
    fieldTax = $("#tax");
    fieldTotal = $("#total");

    formatOrderAmounts();
    formatProductAmounts();

    $("#productList").on("change", ".quantity-input", function (e) {
        updateSubtotalWhenQuantityChanged($(this));
        updateOrderAmounts();
    });

    $("#productList").on("change", ".price-input", function (e) {
        updateSubtotalWhenPriceChanged($(this));
        updateOrderAmounts();
    });

    $("#productList").on("change", ".cost-input", function (e) {
        updateOrderAmounts();
    });

    $("#productList").on("change", ".ship-input", function (e) {
        updateOrderAmounts();
    });
});

/**
 * Updates the order amount fields based on the values in the product list.
 * Calculates the total cost, order subtotal, shipping cost, and order total.
 * These values are displayed in their respective fields and are formatted with thousand separators.
 *
 * @returns {void}
 */
function updateOrderAmounts() {
    totalCost = 0.0;

    $(".cost-input").each(function (e) {
        costInputField = $(this);
        rowNumber = costInputField.attr("rowNumber");
        quantityValue = $("#quantity" + rowNumber).val();


        productCost = getNumberValueRemovedThousandSeparator(costInputField);
        totalCost += productCost * parseInt(quantityValue);
    });

    setAndFormatNumberForField("productCost", totalCost);

    orderSubtotal = 0.0;

    $(".subtotal-output").each(function (e) {
        productSubtotal = getNumberValueRemovedThousandSeparator($(this));
        orderSubtotal += productSubtotal;
    });

    setAndFormatNumberForField("subtotal", orderSubtotal);

    shippingCost = 0.0;

    $(".ship-input").each(function (e) {
        productShip = getNumberValueRemovedThousandSeparator($(this));
        shippingCost += productShip;
    });

    setAndFormatNumberForField("shippingCost", shippingCost);

    tax = getNumberValueRemovedThousandSeparator(fieldTax);
    orderTotal = orderSubtotal + tax + shippingCost;
    setAndFormatNumberForField("total", orderTotal);
}

/**
 * Formats a number to a string with 2 decimal places using the jQuery Number plugin
 * and sets the value of a field with the formatted string.
 *
 * @param {string} fieldId - The ID of the field to set the formatted value to.
 * @param {number} fieldValue - The value to format and set to the field.
 */
function setAndFormatNumberForField(fieldId, fieldValue) {
    formattedValue = $.number(fieldValue, 2);
    $("#" + fieldId).val(formattedValue);
}

/**
 * Gets the value of a number field, removes any thousand separators (commas),
 * and returns the result as a float.
 *
 * @param fieldRef - A reference to the number field as a jQuery object.
 * @returns {number} The value of the number field as a float.
 */
function getNumberValueRemovedThousandSeparator(fieldRef) {
    fieldValue = fieldRef.val().replace(",", "");
    return parseFloat(fieldValue);
}

/**
 * Updates the subtotal for a product when the unit price is changed by
 * multiplying the quantity by the new price, and updating the subtotal field.
 *
 * @param input - A reference to the price field as a jQuery object.
 */
function updateSubtotalWhenPriceChanged(input) {
    priceValue = getNumberValueRemovedThousandSeparator(input);
    rowNumber = input.attr("rowNumber");

    quantityField = $("#quantity" + rowNumber);
    quantityValue = quantityField.val();
    newSubtotal = parseFloat(quantityValue) * priceValue;

    setAndFormatNumberForField("subtotal" + rowNumber, newSubtotal);
}

/**
 * Update the subtotal when the quantity is changed.
 * @param {object} input - The input field that triggered the function.
 */
function updateSubtotalWhenQuantityChanged(input) {
    quantityValue = input.val();
    rowNumber = input.attr("rowNumber");
    priceValue = getNumberValueRemovedThousandSeparator($("#price" + rowNumber));
    newSubtotal = parseFloat(quantityValue) * priceValue;

    setAndFormatNumberForField("subtotal" + rowNumber, newSubtotal);
}

/**
 * Format the numerical values for the product amounts.
 */
function formatProductAmounts() {
    $(".cost-input").each(function (e) {
        formatNumberForField($(this));
    });
    $(".price-input").each(function (e) {
        formatNumberForField($(this));
    });
    $(".subtotal-output").each(function (e) {
        formatNumberForField($(this));
    });
    $(".ship-input").each(function (e) {
        formatNumberForField($(this));
    });
}

/**
 * Format the numerical values for the order amounts.
 */
function formatOrderAmounts() {
    formatNumberForField(fieldProductCost);
    formatNumberForField(fieldSubtotal);
    formatNumberForField(fieldShippingCost);
    formatNumberForField(fieldTax);
    formatNumberForField(fieldTotal);
}


/**
 * Formats a number with two decimal places and updates the value of the specified field.
 * @param {Object} fieldRef - The jQuery object for the field to be formatted.
 */
function formatNumberForField(fieldRef) {
    fieldRef.val($.number(fieldRef.val(), 2));
}

/**
 * Removes thousand separators from the values of specific fields before form submission.
 */
function processFormBeforeSubmit() {
    setCountryName();

    removeThousandSeparatorForField(fieldProductCost);
    removeThousandSeparatorForField(fieldSubtotal);
    removeThousandSeparatorForField(fieldShippingCost);
    removeThousandSeparatorForField(fieldTax);
    removeThousandSeparatorForField(fieldTotal);

    $(".cost-input").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

    $(".price-input").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

    $(".subtotal-output").each(function (e) {
        removeThousandSeparatorForField($(this));
    });

    $(".ship-input").each(function (e) {
        removeThousandSeparatorForField($(this));
    });
}

/**
 * Removes thousand separators from the value of the specified field.
 * @param {Object} fieldRef - The jQuery object for the field to be updated.
 */
function removeThousandSeparatorForField(fieldRef) {
    fieldRef.val(fieldRef.val().replace(",", ""));
}

/**
 * Sets the name of the selected country in the countryName field.
 */
function setCountryName() {
    selectedCountry = $("#country option:selected");
    countryName = selectedCountry.text();
    $("#countryName").val(countryName);
}