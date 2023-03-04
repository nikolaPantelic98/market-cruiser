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

function updateSubtotal(updatedSubtotal, productId) {
    formattedSubtotal = $.number(updatedSubtotal, 2);
    $("#subtotal" + productId).text(formattedSubtotal);
}

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

function showEmptyShoppingCart() {
    $("#section-total").hide();
    $("#section-empty-cart-message").removeClass("d-none");
}

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

function removeProductHTML(rowNumber) {
    $("#row" + rowNumber).remove();
    $("#blank-line" + rowNumber).remove();
}

function updateCountNumbers() {
    $(".div-count").each(function (index, element) {
        element.innerHTML = "" + (index + 1);
    });
}