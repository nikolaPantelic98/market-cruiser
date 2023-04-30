/**
 * This code block sets up event listeners for two buttons with classes .link-minus and .link-plus.
 * When clicked, the .link-minus button decreases the quantity of a product
 * and the .link-plus button increases the quantity of a product.
 */
$(document).ready(function () {
    $(".link-minus").on("click", function (event) {
        event.preventDefault();
        productId = $(this).attr("pid");
        quantityInput = $("#quantity" + productId);
        newQuantity = parseInt(quantityInput.val()) - 1;

        if (newQuantity > 0) {
            quantityInput.val(newQuantity);
        } else {
            showWarningModal('Minimum quantity is 1');
        }
    });

    $(".link-plus").on("click", function (event) {
        event.preventDefault();
        productId = $(this).attr("pid");
        quantityInput = $("#quantity" + productId);
        newQuantity = parseInt(quantityInput.val()) + 1;

        if (newQuantity <= 5) {
            quantityInput.val(newQuantity);
        } else {
            showWarningModal('Maximum quantity is 5');
        }
    });
});