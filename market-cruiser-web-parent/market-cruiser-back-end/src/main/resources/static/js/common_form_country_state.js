var dropdownCountries;
var dropdownStates;

/**
 * Handles the behavior of a dropdown menu for selecting states/provinces based on the selected country.
 * Attaches an event listener to the country dropdown to load the states/provinces for the selected country and empty the states dropdown.
 * Calls loadStates4Country() to populate the states dropdown with the initial states for the first country on page load.
 */
$(document).ready(function() {
    dropdownCountries = $("#country");
    dropdownStates = $("#listStates");

    dropdownCountries.on("change", function() {
        loadStates4Country();
        $("#state").val("").focus();
    });

    loadStates4Country();
});

/**
 * Populates the states/provinces dropdown with the states/provinces for the selected country.
 * Retrieves the selected country value from the country dropdown and constructs a URL to retrieve the corresponding states/provinces.
 * Makes an AJAX GET request to retrieve the states/provinces and populates the states/provinces dropdown with the response.
 * Displays an error modal if there was a problem retrieving the states/provinces for the selected country.
 */
function loadStates4Country() {
    selectedCountry = $("#country option:selected");
    countryId = selectedCountry.val();

    url = contextPath + "states/list_by_country/" + countryId;

    $.get(url, function(responseJson) {
        dropdownStates.empty();

        $.each(responseJson, function(index, state) {
            $("<option>").val(state.name).text(state.name).appendTo(dropdownStates);
        });
    }).fail(function() {
        showErrorModal("Error loading states/provinces for the selected country.");
    })
}