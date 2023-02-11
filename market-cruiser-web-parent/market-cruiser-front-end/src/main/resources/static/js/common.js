function clearFilter() {
    window.location = moduleURL;
}

$('.search-button').click(function(){
    $(this).parent().toggleClass('open');
});

$('.btn-search').click(function () {
    $('.input-search').focus();
});