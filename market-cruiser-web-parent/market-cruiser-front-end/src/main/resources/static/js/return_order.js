var returnModal;
var modalTitle;
var fieldNote;
var orderId;
var divReason;
var divMessage;
var firstButton;
var secondButton;

$(document).ready(function () {
    returnModal = $("#returnOrderModal");
    modalTitle = $("#returnOrderModalTitle");
    fieldNote = $("#returnNote");
    divReason = $("#divReason");
    divMessage = $("#divMessage");
    firstButton = $("#firstButton");
    secondButton = $("#secondButton");

    handleReturnOrderLink();
});

/**
 * Displays a modal dialog for returning an order.
 *
 * @param {jQuery} link - The link to trigger the return modal dialog.
 */
function showReturnModalDialog(link) {
    divMessage.hide();
    divReason.show();
    firstButton.show();
    secondButton.text("Cancel");
    fieldNote.val("");

    orderId = link.attr("orderId");
    returnModal.modal("show");
    modalTitle.text("Return Order ID #" + orderId);
}

/**
 * Displays a modal dialog with a message.
 *
 * @param {string} message - The message to display.
 */
function showMessageModal(message) {
    divReason.hide();
    firstButton.hide();
    secondButton.text("Close");
    divMessage.text(message);

    divMessage.show();
}

/**
 * Handles the click event of the "return order" link.
 *
 * @param {jQuery} link - The link to handle.
 */
function handleReturnOrderLink(link) {
    $(".linkReturnOrder").on("click", function (e) {
        e.preventDefault();
        showReturnModalDialog($(this));
    });
}

/**
 * Submits the return order form and sends a return request to the server.
 *
 * @returns {boolean} - Returns `false` to prevent the form from submitting.
 */
function submitReturnOrderForm() {
    reason = $("input[name='returnReason']:checked").val();
    note = fieldNote.val();

    sendReturnOrderRequest(reason, note);

    return false;
}

/**
 * Sends a return request to the server.
 *
 * @param {string} reason - The reason for the return.
 * @param {string} note - Any notes to include with the return request.
 */
function sendReturnOrderRequest(reason, note) {
    requestURL = contextPath + "orders/return";
    requestBody = {orderId: orderId, reason: reason, note: note};

    $.ajax({
        type: "POST",
        url: requestURL,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(requestBody),
        contentType: 'application/json'

    }).done(function (returnResponse) {
        console.log(returnResponse);
        showMessageModal("Return request has been sent");
        updateStatusTextAndHideReturnButton(orderId);
    }).fail(function (err) {
        console.log(err);
        showMessageModal(err.responseText);
    });
}

/**
 * Updates the status text and hides the return button for a specific order.
 *
 * @param {number} orderId - The ID of the order to update.
 */
function updateStatusTextAndHideReturnButton(orderId) {
    $(".textOrderStatus" + orderId).each(function (index) {
        $(this).text("RETURN_REQUESTED");
    });

    $(".linkReturn" + orderId).each(function (index) {
        $(this).hide();
    });
}