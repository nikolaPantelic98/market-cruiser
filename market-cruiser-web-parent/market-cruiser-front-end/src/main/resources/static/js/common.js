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

$(document).ready(function() {
    $('#buttonAddToCart').click(function(e) {
        e.preventDefault(); // Prevent the default form submission behavior

        var productImage = $('#bigImage');
        var cloneImage = productImage.clone().css({
            position: 'absolute',
            top: productImage.offset().top,
            left: productImage.offset().left
        });

        $('body').append(cloneImage);

        cloneImage.animate({
            top: '5%',
            left: '57%',
            opacity: 0,
            width: '50px',  // Adjust the desired smaller width
            height: '50px'  // Adjust the desired smaller height
        }, 1000, function() {
            cloneImage.remove();
            // Perform the desired action after the animation (e.g., show a success message)
        });
    });
});