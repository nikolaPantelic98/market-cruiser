var buttonLoad4States;
var dropDownCountry4States;
var dropDownStates;
var buttonAddState;
var buttonUpdateState;
var buttonDeleteState;
var labelStateName;
var fieldStateName;

$(document).ready(function() {
    buttonLoad4States = $("#buttonLoadCountriesForStates");
    dropDownCountry4States = $("#dropDownCountriesForStates");
    dropDownStates = $("#dropDownStates");
    buttonAddState = $("#buttonAddState");
    buttonUpdateState = $("#buttonUpdateState");
    buttonDeleteState = $("#buttonDeleteState");
    labelStateName = $("#labelStateName");
    fieldStateName = $("#fieldStateName");

    buttonLoad4States.click(function() {
        loadCountries4States();
    });

    dropDownCountry4States.on("change", function() {
        loadStates4Country();
    });

    dropDownStates.on("change", function() {
        changeFormStateToSelectedState();
    });

    buttonAddState.click(function() {
        if (buttonAddState.val() == "Add") {
            addState();
        } else {
            changeFormStateToNew();
        }
    });

    buttonUpdateState.click(function() {
        updateState();
    });

    buttonDeleteState.click(function() {
        deleteState();
    });
});

/**
 * This function is used to delete a state by sending an AJAX DELETE request to the server.
 * It gets the value of the selected state ID from a dropdown, constructs the URL for the DELETE request,
 * and sends the request with the CSRF token included in the header.
 * If the request is successful, the corresponding option in the dropdown is removed and a success toast message is displayed.
 * If the request fails, an error toast message is displayed.
 */
function deleteState() {
    stateId = dropDownStates.val();

    url = contextPath + "states/delete/" + stateId;

    $.ajax({
        type: 'DELETE',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function() {
        $("#dropDownStates option[value='" + stateId + "']").remove();
        changeFormStateToNew();
        showToastMessageState("The state has been deleted");
    }).fail(function() {
        showToastMessageState("ERROR: Could not connect to server or server encountered an error");
    });
}

/**
 * This function is used to update a state by sending an AJAX POST request to the server with JSON data.
 * It first validates the form input, then constructs the URL for the POST request, and prepares the JSON data to be sent.
 * The AJAX request includes the CSRF token in the header and the JSON data in the request body.
 * If the request is successful, the corresponding option in the dropdown is updated with the new state name, the form state is changed to "New", and a success toast message is displayed.
 * If the request fails, an error toast message is displayed.
 */
function updateState() {
    if (!validateFormState()) return;

    url = contextPath + "states/save";
    stateId = dropDownStates.val();
    stateName = fieldStateName.val();

    selectedCountry = $("#dropDownCountriesForStates option:selected");
    countryId = selectedCountry.val();
    countryName = selectedCountry.text();

    jsonData = {stateId: stateId, name: stateName, country: {countryId: countryId, name: countryName}};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function(stateId) {
        $("#dropDownStates option:selected").text(stateName);
        showToastMessageState("The state has been updated");
        changeFormStateToNew();
    }).fail(function() {
        showToastMessageState("ERROR: Could not connect to server or server encountered an error");
    });
}

/**
 * Adds a new state to the system.
 *
 * This function first validates the state form using the `validateFormState()` function.
 * It then sends an AJAX POST request to the server with the state name and country ID and name.
 * If the request is successful, the `selectNewlyAddedState()` function is called to select the newly added state.
 * Otherwise, a message is displayed to indicate that there was an error.
 */
function addState() {
    if (!validateFormState()) return;

    url = contextPath + "states/save";
    stateName = fieldStateName.val();

    selectedCountry = $("#dropDownCountriesForStates option:selected");
    countryId = selectedCountry.val();
    countryName = selectedCountry.text();

    jsonData = {name: stateName, country: {countryId: countryId, name: countryName}};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function(stateId) {
        selectNewlyAddedState(stateId, stateName);
        showToastMessageState("The new state has been added");
    }).fail(function() {
        showToastMessageState("ERROR: Could not connect to server or server encountered an error");
    });

}

/**
 * Validates the state form to ensure that all required fields have been filled out
 *
 * @return {boolean} - Returns true if the form is valid, false otherwise
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
 * Adds a new state to the drop-down list and selects it.
 *
 * @param {number} stateId - The ID of the newly added state.
 * @param {string} stateName - The name of the newly added state.
 */
function selectNewlyAddedState(stateId, stateName) {
    $("<option>").val(stateId).text(stateName).appendTo(dropDownStates);

    $("#dropDownStates option[value='" + stateId + "']").prop("selected", true);

    fieldStateName.val("").focus();
}

/**
 * Changes the state form to a new state, clearing the input field and disabling
 * the update and delete buttons.
 */
function changeFormStateToNew() {
    buttonAddState.val("Add");
    labelStateName.text("State/Province Name:");

    buttonUpdateState.prop("disabled", true);
    buttonDeleteState.prop("disabled", true);

    fieldStateName.val("").focus();
}

/**
 * Changes the state form to a selected state, filling in the input field with the
 * name of the selected state and enabling the update and delete buttons.
 */
function changeFormStateToSelectedState() {
    buttonAddState.prop("value", "New");
    buttonUpdateState.prop("disabled", false);
    buttonDeleteState.prop("disabled", false);

    labelStateName.text("Selected State/Province:");

    selectedStateName = $("#dropDownStates option:selected").text();
    fieldStateName.val(selectedStateName);

}

/**
 * Loads all states for the currently selected country in the state form.
 * Empties the drop-down list of states and populates it with the states for the selected country.
 * Displays a toast message indicating success or failure.
 */
function loadStates4Country() {
    selectedCountry = $("#dropDownCountriesForStates option:selected");
    countryId = selectedCountry.val();
    url = contextPath + "states/list_by_country/" + countryId;

    $.get(url, function(responseJSON) {
        dropDownStates.empty();

        $.each(responseJSON, function(index, state) {
            $("<option>").val(state.stateId).text(state.name).appendTo(dropDownStates);
        });

    }).done(function() {
        changeFormStateToNew();
        showToastMessageState("All states have been loaded for country " + selectedCountry.text());
    }).fail(function() {
        showToastMessageState("ERROR: Could not connect to server or server encountered an error");
    });
}

/**
 * Loads all countries for the state form.
 * Empties the drop-down list of countries and populates it with the available countries.
 * Displays a toast message indicating success or failure.
 */
function loadCountries4States() {
    url = contextPath + "countries/list";
    $.get(url, function(responseJSON) {
        dropDownCountry4States.empty();

        $.each(responseJSON, function(index, country) {
            $("<option>").val(country.countryId).text(country.name).appendTo(dropDownCountry4States);
        });

    }).done(function() {
        buttonLoad4States.val("Refresh Countries");
        showToastMessageState("All countries have been loaded");
    }).fail(function() {
        showToastMessageState("ERROR: Could not connect to server or server encountered an error");
    });
}

/**
 * Displays a toast message with the specified message.
 */
function showToastMessageState(message) {
    $("#toastMessageState").text(message);
    $(".toast").toast('show');
}