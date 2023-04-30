dropdownBrands = $("#brand");
dropdownCategories = $("#category");

$(document).ready(function () {

    $("#shortDescription").richText();
    $("#fullDescription").richText();

    dropdownBrands.change(function () {
        dropdownCategories.empty();
        getCategories();
    });

    getCategoriesForNewForm();

});

/**
 * This function is used to retrieve a list of categories for a new form.
 * If the form is in edit mode (i.e., there is an existing categoryId field),
 * it will not retrieve the categories. Otherwise, it will call the getCategories function
 * to retrieve the list of categories and populate them in the dropdown menu.
 */
function getCategoriesForNewForm() {
    catIdField = $("#categoryId");
    editMode = false;

    if (catIdField.length) {
        editMode = true;
    }

    if (!editMode) getCategories();
}

/**
 * This function is used to retrieve the list of categories from the server
 * based on the selected brandId, and populate them in the dropdownCategories element.
 */
function getCategories() {
    brandId = dropdownBrands.val();
    url = brandModuleURL + "/" + brandId + "/categories";

    $.get(url, function (responseJson) {
        $.each(responseJson, function (index, category) {
            $("<option>").val(category.categoryId).text(category.name).appendTo(dropdownCategories);
        });
    });
}

/**
 * This function is used to check if the product name entered in the form is unique by sending an AJAX POST request to the server.
 * If the product name is unique, the form will be submitted. If the product name is a duplicate, a warning modal will be displayed.
 * If there is an error during the AJAX request, an error modal will be displayed.
 *
 * @param {HTMLFormElement} form - The form element to be submitted if the product name is unique.
 * @returns {boolean} - Always returns false to prevent the default form submission behavior.
 */
function checkUnique(form) {
    productNewId = $("#productId").val();
    productNewName = $("#name").val();

    csrfValue = $("input[name = '_csrf']").val();

    params = {productId: productNewId, name: productNewName, _csrf: csrfValue};

    $.post(checkUniqueUrl, params, function (response) {
        if (response == "OK") {
            form.submit();
        } else if (response == "Duplicate") {
            showWarningModal("There is another product having the same name " + productNewName);
        } else {
            showErrorModal("Unknown response from server");
        }

    }).fail(function () {
        showErrorModal("Could not connect to the server");
    });

    return false;
}