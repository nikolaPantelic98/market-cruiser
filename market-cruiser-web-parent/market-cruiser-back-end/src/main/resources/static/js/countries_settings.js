var buttonLoad;
var dropdownCountry;
var buttonAddCountry;
var buttonUpdateCountry;
var buttonDeleteCountry;
var labelCountryName;
var fieldCountryName;
var fieldCountryCode;

/**
 * This function is called when the document is loaded and sets up the event listeners for the page.
 * It assigns DOM elements to the global variables and sets up click and change event listeners
 * for the various buttons and input fields.
 */
$(document).ready(function () {
    buttonLoad = $("#buttonLoadCountries");
    dropdownCountry = $("#dropdownCountries");
    buttonAddCountry = $("#buttonAddCountry");
    buttonUpdateCountry = $("#buttonUpdateCountry");
    buttonDeleteCountry = $("#buttonDeleteCountry");
    labelCountryName = $("#labelCountryName");
    fieldCountryName = $("#fieldCountryName");
    fieldCountryCode = $("#fieldCountryCode");

    buttonLoad.click(function () {
        loadCountries();
    });

    dropdownCountry.on("change", function () {
        changeFormStateToSelectedCountry();
    });

    buttonAddCountry.click(function () {
        if (buttonAddCountry.val() == "Add") {
            addCountry();
        } else {
            changeFormStateToNewCountry();
        }
    });

    buttonUpdateCountry.click(function () {
        updateCountry();
    });

    buttonDeleteCountry.click(function () {
        deleteCountry();
    });
});

/**
 * This function deletes the selected country from the list of countries. It gets the selected
 * country's ID and sends a DELETE request to the server to delete it. If the request is successful,
 * it removes the country from the dropdown list and displays a success message. If the request fails,
 * it displays an error message.
 */
function deleteCountry() {
    optionValue = dropdownCountry.val();
    countryId = optionValue.split("-")[0];

    url = contextPath + "countries/delete/" + countryId;

    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function () {
        $("#dropdownCountries option[value='" + optionValue + "']").remove();
        changeFormStateToNewCountry();
        showToastMessage("The country has been deleted");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

/**
 * This function updates the selected country in the list of countries. It first validates the
 * input fields, then sends a POST request to the server to update the country. If the request is
 * successful, it updates the country in the dropdown list and displays a success message. If the
 * request fails, it displays an error message.
 */
function updateCountry() {
    if (!validateFormCountry()) return;

    url = contextPath + "countries/save";
    countryName = fieldCountryName.val();
    countryCode = fieldCountryCode.val();

    countryId = dropdownCountry.val().split("-")[0];

    jsonData = {countryId: countryId, name: countryName, code: countryCode};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function(countryId) {
        $("#dropdownCountries option:selected").val(countryId + "-" + countryCode);
        $("#dropdownCountries option:selected").text(countryName);
        showToastMessage("The country has been updated");

        changeFormStateToNewCountry();
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

/**
 * Validates the country form before submitting to add or update a country.
 * If the form is not valid, it displays an error message and returns false.
 * Otherwise, it returns true.
 */
function validateFormCountry() {
    formCountry = document.getElementById("formCountry");
    if (!formCountry.checkValidity()) {
        formCountry.reportValidity();
        return false;
    }
    return true;
}

/**
 * Sends a request to add a new country to the server and updates the dropdown list of countries if successful.
 * If the form fields are invalid, displays a validation error message.
 */
function addCountry() {
    if (!validateFormCountry()) return;

    url = contextPath + "countries/save";
    countryName = fieldCountryName.val();
    countryCode = fieldCountryCode.val();
    jsonData = {name: countryName, code: countryCode};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function(countryId) {
        selectNewlyAddedCountry(countryId, countryCode, countryName);
        showToastMessage("The new country has been added");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

/**
 * Validates the form fields for adding/updating a state.
 * If the form fields are invalid, displays a validation error message.
 * @returns {boolean} true if the form fields are valid, false otherwise
 */
function validateFormState() {
    formState = document.getElementById("formState");
    if (!formState.checkValidity()) {
        formState.reportValidity();
        return false;
    }
    return true;
}

/**
 * Adds the newly added country to the dropdown list of countries and selects it.
 * @param {string} countryId - The ID of the newly added country
 * @param {string} countryCode - The code of the newly added country
 * @param {string} countryName - The name of the newly added country
 */
function selectNewlyAddedCountry(countryId, countryCode, countryName) {
    optionValue = countryId + "-" + countryCode;
    $("<option>").val(optionValue).text(countryName).appendTo(dropdownCountry);

    $("#dropdownCountries option[value='" + optionValue + "']").prop("selected", true);

    fieldCountryCode.val("");
    fieldCountryName.val("").focus();
}

/**
 * Changes the state of the country form to a new country by resetting the form fields and disabling update/delete buttons.
 */
function changeFormStateToNewCountry() {
    buttonAddCountry.val("Add");
    labelCountryName.text("Country Name:");
    buttonUpdateCountry.prop("disabled", true);
    buttonDeleteCountry.prop("disabled", true);

    fieldCountryCode.val("");
    fieldCountryName.val("").focus();
}

/**
 * Changes the state of the country form to a selected country by enabling update/delete buttons
 * and pre-filling the form fields with selected country data.
 */
function changeFormStateToSelectedCountry() {
    buttonAddCountry.prop("value", "New");
    buttonUpdateCountry.prop("disabled", false);
    buttonDeleteCountry.prop("disabled", false);

    labelCountryName.text("Selected Country:");

    selectedCountryName = $("#dropdownCountries option:selected").text();
    fieldCountryName.val(selectedCountryName);

    countryCode = dropdownCountry.val().split("-")[1];
    fieldCountryCode.val(countryCode);
}

/**
 * Loads all countries from the server and populates the dropdown list with the retrieved data.
 * Also updates the label of the "Load" button and shows a toast message after the operation completes.
 */
function loadCountries() {
    url = contextPath + "countries/list";
    $.get(url, function (responseJSON) {
        dropdownCountry.empty();

        $.each(responseJSON, function (index, country) {
            optionValue = country.countryId + "-" + country.code;
            $("<option>").val(optionValue).text(country.name).appendTo(dropdownCountry);
        });

    }).done(function () {
        buttonLoad.val("Refresh Country List");
        showToastMessage("All countries have been loaded");
    }).fail(function () {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    });
}

/**
 * Shows a toast message with the given text message.
 */
function showToastMessage(message) {
    $("#toastMessage").text(message);
    $(".toast").toast('show');
}