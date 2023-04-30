var iconNames = {
    'PICKED':'fa-people-carry',
    'SHIPPING':'fa-shipping-fast',
    'DELIVERED':'fa-box-open',
    'RETURNED':'fa-undo'
};
var confirmText;
var confirmModalDialog;
var yesButton;
var noButton;

/**
 * Initializes the confirmation modal and sets event handlers for relevant buttons.
 */
$(document).ready(function() {
    confirmText = $("#confirmText");
    confirmModalDialog = $("#confirmModal");
    yesButton = $("#yesButton");
    noButton = $("#noButton");

    $(".linkUpdateStatus").on("click", function (e) {
        e.preventDefault();
        link = $(this);
        showUpdateConfirmModal(link);
    });

    addEventHandlerForYesButton();
});

/**
 * Displays a confirmation modal dialog to confirm the user's intention to update the status of an order.
 * @param {jQuery} link - The link element that was clicked to initiate the update.
 */
function showUpdateConfirmModal(link) {
    noButton.text("No");
    yesButton.show();

    orderId = link.attr("orderId");
    status = link.attr("status");
    yesButton.attr("href", link.attr("href"));

    confirmText.text("Are you sure you want to update the status of the order ID #" + orderId + "" +
        " to " + status + "?");

    confirmModalDialog.modal();
}

/**
 * Adds an event handler for the "Yes" button in the confirmation modal dialog.
 */
function addEventHandlerForYesButton() {
    yesButton.click(function (e) {
        e.preventDefault();
        sendRequestToUpdateOrderStatus($(this));
    });
}

/**
 * Sends an AJAX request to update the status of an order.
 * @param {jQuery} button - The button element that was clicked to initiate the update.
 */
function sendRequestToUpdateOrderStatus(button) {
    requestURL = button.attr("href");

    $.ajax({
        type: 'POST',
        url: requestURL,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function(response) {
        showMessageModal("Order updated successfully");
        updateStatusIconColor(response.orderId, response.status);
        console.log(response);
    }).fail(function (err) {
        showMessageModal("Error updating order status");
    })
}

/**
 * Updates the status icon color of an order based on the new status.
 * @param {string} orderId - The ID of the order.
 * @param {string} status - The new status of the order.
 */
function updateStatusIconColor(orderId, status) {
    link = $("#link" + status + orderId);
    link.replaceWith("<i class='fas " + iconNames[status] + " fa-2x icon-orange'></i>")
}

/**
 * Displays a modal with a message to the user.
 * @param {string} message - The message to display in the modal.
 */
function showMessageModal(message) {
    noButton.text("Close");
    yesButton.hide();
    confirmText.text(message);
}