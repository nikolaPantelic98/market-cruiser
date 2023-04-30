var dropdownCountry;
var dataListState;
var fieldState;

$(document).ready(function () {
    dropdownCountry = $("#country");
    dataListState = $("#listStates");
    fieldState = $("#state");

    dropdownCountry.on("change", function () {
        loadStatesForCountry();
        fieldState.val("").focus();
    });
});

/**
 * This function retrieves a list of states for a selected country using an AJAX GET request.
 * It populates a dropdown list with the retrieved states.
 */
function loadStatesForCountry() {
    selectedCountry = $("#country option:selected");
    countryId = selectedCountry.val();
    url = contextPath + "settings/list_states_by_country/" + countryId;

    $.get(url, function (responseJSON) {
        dataListState.empty();

        $.each(responseJSON, function (index, state) {
            $("<option>").val(state.name).text(state.name).appendTo(dataListState);
        });

    }).fail(function () {
        alert('failed to connect to the server!');
    });
}

/**
 * This function checks whether the entered password and confirmation password match.
 * It sets a custom validation message on the confirmation password input field based on the result.
 * This function is called on the "input" event of the confirmation password input field.
 * @param {HTMLInputElement} confirmPassword - The confirmation password input field element
 */
function checkPasswordMatch(confirmPassword) {
    if (confirmPassword.value != $("#password").val()) {
        confirmPassword.setCustomValidity("Passwords do not match!");
    } else {
        confirmPassword.setCustomValidity("");
    }
}