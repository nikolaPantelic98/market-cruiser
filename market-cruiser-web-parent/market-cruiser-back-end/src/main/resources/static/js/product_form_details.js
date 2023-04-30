/**
 * On document ready, loop through each anchor element with a name attribute equal to "linkRemoveDetail".
 * Attach a click event handler to each anchor element, which calls the removeDetailSectionByIndex function
 * with the index of the clicked element.
 */
$(document).ready(function () {
    $("a[name='linkRemoveDetail']").each(function (index) {
        $(this).click(function () {
            removeDetailSectionByIndex(index);
        });
    });
});

/**
 * Adds a new product detail section to the form.
 * This function creates a new div with inputs for the name and value of the product detail,
 * and appends it to the div with id "divProductDetails".
 * Also adds a link to remove the section.
 */
function addNextDetailSection() {
    allDivDetails = $("[id^='divDetail']");
    divDetailsCount = allDivDetails.length;

    htmlDetailSection = `
        <div class="form-inline" id="divDetail${divDetailsCount}">
            <input type="hidden" name="detailIDs" value="0">
            <label class="m-3" style="color: white">Name:</label>
            <input type="text" class="form-control w-25" name="detailNames" maxlength="255" />
            <label class="m-3" style="color: white">Value:</label>
            <input type="text" class="form-control w-25" name="detailValues" maxlength="255" />
        </div>
    `;

    $("#divProductDetails").append(htmlDetailSection);

    previousDivDetailSection = allDivDetails.last();
    previousDivDetailID = previousDivDetailSection.attr("id");

    htmlLinkRemove = `
    &nbsp;&nbsp;&nbsp;
    <a class="a-icon-lightgreen fas fa-times-circle fa-1x icon-lightgreen"
        href="javascript:removeDetailSectionById('${previousDivDetailID}')"
        title="Remove this detail"></a>
    `;

    previousDivDetailSection.append(htmlLinkRemove);

    $("input[name='detailNames']").last().focus();
}

/**
 * Removes a product detail section from the form by its ID.
 * @param {string} id - The ID of the detail section to remove.
 */
function removeDetailSectionById(id) {
    $("#" + id).remove();
}

/**
 * Removes a product detail section from the form by its index.
 * @param {number} index - The index of the detail section to remove.
 */
function removeDetailSectionByIndex(index) {
    $("#divDetail" + index).remove();
}