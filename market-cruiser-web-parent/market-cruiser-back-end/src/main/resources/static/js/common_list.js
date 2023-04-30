/**
 * Clears the filters and resets the search results to the default state.
 */
function clearFilter() {
    window.location = moduleURL;
}

/**
 * Shows a modal dialog to confirm the deletion of an entity.
 * @param {Object} link - The delete button link.
 * @param {string} entityName - The name of the entity to be deleted.
 */
function showDeleteConfirmModal(link, entityName) {
        entityId = link.attr("entityId");

    $("#yesButton").attr("href", link.attr("href"));
    $("#confirmText").text("Are you sure you want to delete this " + entityName + " with ID " + entityId + "?");
    $("#confirmModal").modal();
}

/**
 * Toggles the visibility of the search input field.
 */
$('.search-button').click(function(){
    $(this).parent().toggleClass('open');
});

/**
 * Sets the focus on the search input field when the search button is clicked.
 */
$('.btn-search').click(function () {
    $('.input-search').focus();
});