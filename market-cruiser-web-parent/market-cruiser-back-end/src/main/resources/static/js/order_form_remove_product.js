/*
This function sets up a click event listener on the "remove" links for each product in the product list.
When a "remove" link is clicked, the function checks if the order has only one product. If it does,
a warning modal is shown. Otherwise, the product is removed from the list, and the order amounts are updated.
*/
$(document).ready(function () {
    $("#productList").on("click", ".link-remove", function (e) {
        e.preventDefault();

        if (doesOrderHaveOnlyOneProduct()) {
            showWarningModal("Could not remove product. The order must have at least one product.");
        } else {
            removeProduct($(this));
            updateOrderAmounts();
        }
    });
});

/*
This function removes a product from the product list. The row number of the product's row
is obtained from the "rowNumber" attribute of the link that was clicked. The corresponding
row and blank line are then removed from the DOM, and the product counts for each row are
updated to reflect the new state of the list.
*/
function removeProduct(link) {
    rowNumber = link.attr("rowNumber");
    $("#row" + rowNumber).remove();
    $("#blankLine" + rowNumber).remove();

    $(".divCount").each(function (index, element) {
        element.innerHTML = "" + (index + 1);
    });
}

/*
This function checks if the order has only one product.
If there is only one product, the function returns true. Otherwise, it returns false.
*/
function doesOrderHaveOnlyOneProduct() {
    productCount = $(".hiddenProductId").length;
    return productCount == 1;
}