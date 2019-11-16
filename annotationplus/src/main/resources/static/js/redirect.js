$(document).ready(function() {
    var xhr;
    var _orgAjax = $.ajaxSettings.xhr;
    $.ajaxSettings.xhr = function() {
        xhr = _orgAjax();
        return xhr;
    };

    $(document).ajaxComplete(function() {
        if (xhr.responseURL.indexOf("/login") > -1) {
            $(".formContainer").css("display","none");
            $("#sessionExpiredContainer").css("display","block");
            window.open("/login", "_blank");
        }
    });
});