var extraImagesCount = 0;

$(document).ready(function () {
    $("input[name='extraImage']").each(function (index) {
        extraImagesCount++;

        $(this).change(function () {
            showExtraImageThumbnail(this, index);
        });
    });

    $("a[name='linkRemoveExtraImage']").each(function (index) {
        $(this).click(function () {
            removeExtraImage(index);
        });
    });
});

/**
 * Shows the thumbnail preview of the extra image file selected by the user and updates the file name in the hidden field.
 *
 * @param {Object} fileInput - The input element of type "file" for selecting the extra image file.
 * @param {number} index - The index of the extra image section to which the selected file belongs.
 */
function showExtraImageThumbnail(fileInput, index) {
    var file = fileInput.files[0];

    fileName = file.name;

    imageNameHiddenField = $("#imageName" + index);
    if (imageNameHiddenField.length) {
        imageNameHiddenField.val(fileName);
    }

    var reader = new FileReader();
    reader.onload = function(e) {
        $("#extraThumbnail" + index).attr("src", e.target.result);
    };

    reader.readAsDataURL(file);

    if (index >= extraImagesCount - 1) {
        addNextExtraImageSection(index + 1);
    }
}


function addNextExtraImageSection(index) {
    htmlExtraImage = `
    <div class="col border m-3 p-2" id="divExtraImage${index}">
        <div id="extraImageHeader${index}"><label style="color: white">Extra Image #${index + 1}:</label></div>
        <div class="m-2">
            <img id="extraThumbnail${index}" alt="Extra image #${index + 1} preview" class="img-fluid" src="${defaultImageThumbnailSrc}" />
        </div>
        <div>
            <input type="file" name="extraImage" onchange="showExtraImageThumbnail(this, ${index})" accept="image/png, image/jpeg"/>
        </div>
    </div>
    `;

    htmlLinkRemove = `
    <a class="a-icon-lightgreen fas fa-times-circle fa-1x icon-lightgreen float-right" 
        href="javascript:removeExtraImage(${index - 1})"
        title="Remove this image"></a>
    `;

    $("#divProductImages").append(htmlExtraImage);

    $("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);

    extraImagesCount++;
}

function removeExtraImage(index) {
    $("#divExtraImage" + index).remove();
}