/**
 * Displays a modal dialog to the user with the given title and message.
 *
 * @param {string} title - The title of the modal dialog.
 * @param {string} message - The message to display in the modal dialog.
 */
function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $('#modalDialog').modal();
}

/**
 * Displays a modal dialog to the user with the title "Error" and the given message.
 *
 * @param {string} message - The error message to display in the modal dialog.
 */
function showErrorModal(message) {
    showModalDialog("Error", message);
}

/**
 * Displays a modal dialog to the user with the title "Warning" and the given message.
 *
 * @param {string} message - The warning message to display in the modal dialog.
 */
function showWarningModal(message) {
    showModalDialog("Warning", message);
}