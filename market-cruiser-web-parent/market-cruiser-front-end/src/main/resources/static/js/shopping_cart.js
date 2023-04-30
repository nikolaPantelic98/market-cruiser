$(document).ready(function () {
    $(".link-minus").on("click", function (event) {
        event.preventDefault();
        decreaseQuantity($(this));
    });

    $(".link-plus").on("click", function (event) {
        event.preventDefault();
        increaseQuantity($(this));
    });

    $(".link-remove").on("click", function (event) {
        event.preventDefault();
        removeProduct($(this));
    });
});

/**
 * Decreases the quantity of a product in the cart by 1 and updates the cart subtotal and total.
 *
 * @param {jQuery} link - The jQuery object for the link element that was clicked.
 */
function decreaseQuantity(link) {
    productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) - 1;

    if (newQuantity > 0) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    } else {
        showWarningModal('Minimum quantity is 1');
    }
}

/**
 * Increases the quantity of a product in the cart by 1 and updates the cart subtotal and total.
 *
 * @param {jQuery} link - The jQuery object for the link element that was clicked.
 */
function increaseQuantity(link) {
    productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) + 1;

    if (newQuantity <= 5) {
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    } else {
        showWarningModal('Maximum quantity is 5');
    }
}

/**
 * Updates the quantity of a product in the cart on the server and updates the cart subtotal and total.
 *
 * @param {number} productId - The ID of the product to update.
 * @param {number} quantity - The new quantity for the product.
 */
function updateQuantity(productId, quantity) {
    url = contextPath + "cart/update/" + productId + "/" + quantity;

    $.ajax({
        type: "POST",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (updatedSubtotal) {
        updateSubtotal(updatedSubtotal, productId);
        updateTotal();
    }).fail(function () {
        showErrorModal("Error while updating product quantity.");
    });
}

/**
 * Updates the subtotal of a product in the shopping cart.
 *
 * @param {number} updatedSubtotal - The new subtotal of the product.
 * @param {number} productId - The ID of the product whose subtotal is being updated.
 */
function updateSubtotal(updatedSubtotal, productId) {
    formattedSubtotal = $.number(updatedSubtotal, 2);
    $("#subtotal" + productId).text(formattedSubtotal);
}

/**
 * Updates the total amount of the shopping cart.
 */
function updateTotal() {
    total = 0.0;
    productCount = 0;

    $(".subtotal").each(function(index, element) {
        productCount++;
        total += parseFloat(element.innerHTML.replaceAll(",", ""));
    });

    if (productCount < 1) {
        showEmptyShoppingCart();
    } else {
        formattedTotal = $.number(total, 2);
        $("#total").text(formattedTotal);
    }
}

/**
 * Shows a message indicating that the shopping cart is empty.
 */
function showEmptyShoppingCart() {
    $("#section-total").hide();
    $("#section-empty-cart-message").removeClass("d-none");
}

/**
 * Sends a request to remove a product from the shopping cart
 * @param {object} link - The link to remove the product
 */
function removeProduct(link) {
    url = link.attr("href");

    $.ajax({
        type: "DELETE",
        url: url,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        }
    }).done(function (response) {
        rowNumber = link.attr("rowNumber");
        removeProductHTML(rowNumber);
        updateTotal();
        updateCountNumbers();

        showModalDialog("Shopping Cart", response);

    }).fail(function () {
        showErrorModal("Error while removing product.");
    });
}

/**
 * Removes the HTML elements for a product from the shopping cart
 * @param {number} rowNumber - The row number of the product to remove
 */
function removeProductHTML(rowNumber) {
    $("#row" + rowNumber).remove();
    $("#blank-line" + rowNumber).remove();
}

/**
 * Updates the count numbers for the products in the shopping cart
 */
function updateCountNumbers() {
    $(".div-count").each(function (index, element) {
        element.innerHTML = "" + (index + 1);
    });
}