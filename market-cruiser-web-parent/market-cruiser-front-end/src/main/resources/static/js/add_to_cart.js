$(document).ready(function () {
    $("#buttonAddToCart").on("click", function (event) {
        addToCart();
    });
});

/**
 * This function is called when the user clicks the "Add to Cart" button for a product.
 * It retrieves the quantity of the product that the user wants to add, and sends an AJAX
 * request to the server to add the product to the shopping cart. If the request is successful,
 * it displays the contents of the shopping cart in a modal dialog. If the request fails,
 * it displays an error message in a modal dialog.
 */
function addToCart() {
    quantity = $("#quantity" + productId).val();
    url = contextPath + "cart/add/" + productId + "/" + quantity;

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (response) {
        showModalDialog("Shopping Cart", response);
    }).fail(function () {
        showErrorModal("Error while adding product to shopping cart.");
    });
}