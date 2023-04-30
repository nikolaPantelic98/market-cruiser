var productDetailCount;

/**
 * Executes when the document is ready. Initializes the productDetailCount variable
 * and adds an event listener to the "Add Product" link, which opens a modal with a URL
 * for adding a new product. When the modal is shown, the URL is loaded in an iframe
 */
$(document).ready(function () {
    productDetailCount = $(".hiddenProductId").length;

    $("#products").on("click", "#linkAddProduct", function (e) {
        e.preventDefault();
        link = $(this);
        url = link.attr("href");

        $("#addProductModal").on("shown.bs.modal", function () {
            $(this).find("iframe").attr("src", url);
        });

        $("#addProductModal").modal();
    });
});

/**
 * Calls the getShippingCost function with the given productId parameter
 * @param {number} productId - The ID of the product being added
 * @param {string} productName - The name of the product being added
 */
function addProduct(productId, productName) {
    getShippingCost(productId);
}


/**
 * Calculates the shipping cost for a given product and updates its information in the product list.
 * @param {number} productId - The ID of the product for which the shipping cost needs to be calculated.
 */
function getShippingCost(productId) {
    selectedCountry = $("#country option:selected");
    countryId = selectedCountry.val();

    state = $("#state").val();
    if (state.length == 0) {
        state = $("#city").val();
    }

    requestUrl = contextPath + "get_shipping_cost";
    params = {productId: productId, countryId: countryId, state: state};

    $.ajax({
        type: 'POST',
        url: requestUrl,
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: params

    }).done(function(shippingCost) {
        getProductInfo(productId, shippingCost);
    }).fail(function (err) {
        showWarningModal(err.responseJSON.message);
        shippingCost = 0.0;
        getProductInfo(productId, shippingCost);
    }).always(function () {
        $("#addProductModal").modal("hide");
    });
}

/**
 * Gets the product information from the server and generates HTML code for the product in the product list.
 * @param {number} productId - The ID of the product for which the shipping cost was calculated.
 * @param {number} shippingCost - The calculated shipping cost for the product.
 */
function getProductInfo(productId, shippingCost) {
    requestURL = contextPath + "products/get/" + productId;
    $.get(requestURL, function (productJson) {
        console.log(productJson);
        productName = productJson.name;
        mainImagePath = contextPath.substring(0, contextPath.length - 1) + productJson.imagePath;
        productCost = $.number(productJson.cost, 2);
        productPrice = $.number(productJson.price, 2);

        htmlCode = generateProductCode(productId, productName, mainImagePath, productCost, productPrice, shippingCost);
        $("#productList").append(htmlCode);

        updateOrderAmounts();

    }).fail(function (err) {
        showWarningModal(err.responseJSON.message);
    });
}


/**
 * Generates the HTML code for a single product detail row in a shopping cart.
 *
 * @param {string} productId - The ID of the product.
 * @param {string} productName - The name of the product.
 * @param {string} mainImagePath - The URL of the main image for the product.
 * @param {number} productCost - The cost of the product.
 * @param {number} productPrice - The selling price of the product.
 * @param {number} shippingCost - The shipping cost of the product.
 * @returns {string} The HTML code for the product detail row.
 */
function generateProductCode(productId, productName, mainImagePath, productCost, productPrice, shippingCost) {
    nextCount = productDetailCount + 1;
    productDetailCount++;
    rowId = "row" + nextCount;
    quantityId = "quantity" + nextCount;
    priceId = "price" + nextCount;
    subtotalId = "subtotal" + nextCount;
    blankLineId = "blankLine" + nextCount;

    htmlCode = `
        <div class="border-lightgreen rounded p-1" id="${rowId}">
            <input type="hidden" name="detailId" value="0" />
            <input type="hidden" name="productId" value="${productId}" class="hiddenProductId" />

            <div class="row">
                <div class="col-3" style="display: flex; align-items: center; justify-content: center">
                    <div class="divCount" style="color: white">${nextCount}</div>
                </div>
                <div class="col-6" style="display: flex; align-items: center; justify-content: center">
                    <img src="${mainImagePath}" class="img-fluid" width="200px" />
                </div>
                <div class="col-3" style="display: flex; align-items: center; justify-content: center">
                    <div><a class="a-icon-lightgreen fas fa-trash icon-red fa-2x link-remove" href="" rowNumber="${nextCount}"></a></div>
                </div>
            </div>

            <div class="row m-2" style="justify-content: center">
                <b style="color: #1deba8">${productName}</b>
            </div>
            <br>

            <div class="row m-2">
                <table>
                    <tr>
                        <td style="text-align: center">Product Cost:</td>
                        <td>
                            <input type="text" required class="form-control m-1 cost-input"
                                   name="productDetailCost"
                                   rowNumber="${nextCount}"
                                   value="${productCost}" style="max-width: 140px"/>
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: center">Quantity:</td>
                        <td>
                            <input type="number" step="1" min="1" max="5" class="form-control m-1 quantity-input"
                                   name="quantity"
                                   id="${quantityId}"
                                   rowNumber="${nextCount}"
                                   value="1" style="max-width: 140px"/>
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: center">Unit Price:</td>
                        <td>
                            <input type="text" required class="form-control m-1 price-input"
                                   name="productPrice"
                                   id="${priceId}"
                                   rowNumber="${nextCount}"
                                   value="${productPrice}" style="max-width: 140px"/>
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: center">Subtotal:</td>
                        <td>
                            <input type="text" readonly="readonly" class="form-control m-1 subtotal-output"
                                   name="productSubtotal"
                                   id="${subtotalId}"
                                   value="${productPrice}" style="max-width: 140px"/>
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: center">Shipping Cost:</td>
                        <td>
                            <input type="text" required class="form-control m-1 ship-input"
                                   name="productShipCost"
                                   value="${shippingCost}" style="max-width: 140px"/>
                        </td>
                    </tr>
                </table>
            </div>

        </div>
            <div id="${blankLineId}" class="row">&nbsp;</div>
    `;

    return htmlCode;
}


/**
 * Checks whether a product with the given productId has already been added to the shopping cart.
 *
 * @param {number} productId - The ID of the product to check.
 * @returns {boolean} - True if a product with the given productId has already been added to the cart, false otherwise.
 */
function isProductAlreadyAdded(productId) {
    productExists = false;
    $(".hiddenProductId").each(function (e) {
        aProductId = $(this).val();

        if (aProductId == productId) {
            productExists = true;
            return;
        }
    });

    return productExists;
}