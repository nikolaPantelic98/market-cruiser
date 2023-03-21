function clearFilter() {
    window.location = moduleURL;
}

function showDeleteConfirmModal(link, entityName) {
        entityId = link.attr("entityId");

    $("#yesButton").attr("href", link.attr("href"));
    $("#confirmText").text("Are you sure you want to delete this " + entityName + " with ID " + entityId + "?");
    $("#confirmModal").modal();
}

$('.search-button').click(function(){
    $(this).parent().toggleClass('open');
});

$('.btn-search').click(function () {
    $('.input-search').focus();
});