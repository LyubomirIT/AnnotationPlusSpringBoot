$(document).ready(function () {
    var $loading = $('.loading').hide();
    $(document)
        .ajaxStart(function () {
            $loading.show();
        })
        .ajaxStop(function () {
            $loading.hide();
        });

    $("form").submit(function () {
        $loading.show();
    });
});