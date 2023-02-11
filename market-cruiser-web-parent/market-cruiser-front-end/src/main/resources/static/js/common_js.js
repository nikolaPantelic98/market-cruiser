function clearFilter() {
    window.location = moduleURL;
}

$('.search-button').click(function(){
    $(this).parent().toggleClass('open');
});