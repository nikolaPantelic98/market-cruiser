$(document).ready(function () {
    $("a[name='linkRemoveDetail']").each(function (index) {
        $(this).click(function () {
            removeDetailSectionByIndex(index);
        });
    });
});

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

function removeDetailSectionById(id) {
    $("#" + id).remove();
}

function removeDetailSectionByIndex(index) {
    $("#divDetail" + index).remove();
}