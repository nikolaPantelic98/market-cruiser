/**
 * Clears the filter by reloading the page without any filter parameters.
 */
function clearFilter() {
    window.location = moduleURL;
}

/**
 * Toggles the open/closed state of the search bar dropdown menu when its button is clicked.
 */
$('.search-button').click(function(){
    $(this).parent().toggleClass('open');
});

/**
 * Focuses the search input field when the search button is clicked.
 */
$('.btn-search').click(function () {
    $('.input-search').focus();
});