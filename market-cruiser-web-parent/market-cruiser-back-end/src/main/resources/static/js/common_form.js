/**
 * Waits for the document to finish loading before running any JavaScript code.
 * Adds click event handler to the cancel button that redirects to moduleURL.
 * Adds change event handler to the file input field that checks if the selected file is valid
 * and shows a thumbnail of the image if valid.
 */
$(document).ready(function() {
    $("#buttonCancel").on("click", function() {
        window.location = moduleURL;
    });

    $("#fileImage").change(function() {
        if (!checkFileSize(this)) {
            return;
        }

        showImageThumbnail(this);
    });
});

/**
 * Shows a thumbnail of the selected image in the file input field.
 * @param {Object} fileInput - The file input field that contains the selected image.
 */
function showImageThumbnail(fileInput) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function(e) {
        $("#thumbnail").attr("src", e.target.result);
    };

    reader.readAsDataURL(file);
}

/**
 * Checks if the size of the selected file is less than or equal to 1 MB.
 * Displays an error message if the file size is greater than 1 MB.
 * @param {Object} fileInput - The file input field that contains the selected image.
 * @returns {boolean} - True if the file size is valid, false otherwise.
 */
function checkFileSize(fileInput) {
    fileSize = fileInput.files[0].size;

    if (fileSize > 1048576) {
        fileInput.setCustomValidity("You must choose an image less than 1 MB");
        fileInput.reportValidity();

        return false;
    } else {
        fileInput.setCustomValidity("");

        return true;
    }
}

/**
 * Shows a modal dialog with the specified title and message.
 * @param {string} title - The title of the modal dialog.
 * @param {string} message - The message to be displayed in the modal dialog.
 */
function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $('#modalDialog').modal();
}

/**
 * Shows an error modal dialog with the specified error message.
 * @param {string} message - The error message to be displayed in the modal dialog.
 */
function showErrorModal(message) {
    showModalDialog("Error", message);
}

/**
 * Shows a warning modal dialog with the specified warning message.
 * @param {string} message - The warning message to be displayed in the modal dialog.
 */
function showWarningModal(message) {
    showModalDialog("Warning", message);
}

/**
 * Updates the value of the hidden "enabled" field based on the value of the checkbox.
 */
function updateEnabledField() {
    var checkbox = document.getElementById("checkbox");
    var hiddenField = document.getElementById("enabled");
    hiddenField.value = checkbox.checked;
}

/**
 * Updates the value of the hidden "inStock" field based on the value of the checkbox.
 */
function updateInStockField() {
    var checkbox = document.getElementById("checkbox-instock");
    var hiddenField = document.getElementById("inStock");
    hiddenField.value = checkbox.checked;
}