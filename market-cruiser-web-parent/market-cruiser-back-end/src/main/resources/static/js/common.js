/**
 * This function runs when the document is ready and attaches a click event handler to the logout link to submit the logout form.
 */
$(document).ready(function() {
    $("#logoutLink").on("click", function (e) {
        e.preventDefault();
        document.logoutForm.submit();
    });
    customizeDropdownMenu();
});

/**
 * This function customizes the behavior of dropdown menus.
 * When a dropdown menu is hovered over, it slides down with a delay of 250ms.
 * When the cursor moves away from the dropdown menu, it slides up with a delay of 100ms.
 * When a dropdown menu link is clicked, it navigates to the corresponding URL.
 */
function customizeDropdownMenu() {
    $(".navbar .dropdown").hover(
        function () {
            $(this).find('.dropdown-menu').first().stop(true, true).delay(250).slideDown();
        },
        function () {
            $(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();
        }
    )   ;

    $(".dropdown > a").click(function () {
        location.href = this.href;
    });
}

/**
 * This function toggles the 'open' class of the parent element when the search button is clicked.
 */
$('.search-button').click(function(){
    $(this).parent().toggleClass('open');
});

/**
 * This function focuses the search input when the search button is clicked.
 */
$('.btn-search').click(function () {
    $('.input-search').focus();
});