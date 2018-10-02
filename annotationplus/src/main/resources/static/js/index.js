function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}


$(document).ready(function() {

$("#loginButton").prop("disabled", true);


/*
$('#loginButton').on('click', function(e) {


        if ($.trim($("#email").val()).length > 1 && !validateEmail(email)) {
            $(this).css({
                "border-color": "red",
                "border-width": "3px",
                "border-style": "solid"
            });
            $("#loginButton").prop("disabled", true);
            $("#loginEmailErrorMessage").css("display", "block");
            $("#loginButton").css("cursor", "not-allowed");

        }

        if ($.trim($("#email").val()).length > 0 && validateEmail(email)) {
            $(this).css({
                "border-bottom": "1px solid #ccc",
                "border-left": "1px solid #ccc",
                "border-right": "1px solid #ccc",
                "border-top": "1px solid #ccc",
            });
            //$("#loginButton").prop("disabled", false);
            $("#loginEmailErrorMessage").css("display", "none");
            //$("#loginButton").css("cursor", "pointer");
        }

    });
*/




$('#email').on('input', function(e) {


        if ($.trim($(this).val()).length < 1 || $.trim($("#password").val()).length < 1) {
            /*$(this).css({
                "border-color": "red",
                "border-width": "3px",
                "border-style": "solid"
            });*/
            $("#loginButton").prop("disabled", true);
            //$("#loginEmailErrorMessage").css("display", "block");
            $("#loginButton").css("cursor", "not-allowed");

        }

        if ($.trim($(this).val()).length > 0 && $.trim($("#password").val()).length > 0) {
            /*$(this).css({
                "border-bottom": "1px solid #ccc",
                "border-left": "1px solid #ccc",
                "border-right": "1px solid #ccc",
                "border-top": "1px solid #ccc",
            });*/
            $("#loginButton").prop("disabled", false);
            //$("#loginEmailErrorMessage").css("display", "none");
            $("#loginButton").css("cursor", "pointer");
        }

    });

    $('#password').on('input', function(e) {


        if ($.trim($(this).val()).length < 1 || $.trim($("#email").val()).length < 1) {
            /*$(this).css({
                "border-color": "red",
                "border-width": "3px",
                "border-style": "solid"
            });*/
            $("#loginButton").prop("disabled", true);
            //$("#loginPasswordErrorMessage").css("display", "block");
            $("#loginButton").css("cursor", "not-allowed");

        }

        if ($.trim($(this).val()).length > 0 && $.trim($("#email").val()).length > 0) {
            /*$(this).css({
                "border-bottom": "1px solid #ccc",
                "border-left": "1px solid #ccc",
                "border-right": "1px solid #ccc",
                "border-top": "1px solid #ccc",
            });*/
            $("#loginButton").prop("disabled", false);
           // $("#loginPasswordErrorMessage").css("display", "none");
            $("#loginButton").css("cursor", "pointer");
        }

    });







    });



