function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

$(document).ready(function () {
    $("#requestToken").click(function () {
        var dataObject = {
            "email": $("#email").val()
        };
        var btn = $(this);
        btn.prop('disabled', true);
        $("#success").css("display", "none");
        $("#error").css("display", "none");
        $.ajax({
            type: "POST",
            url: "/api/forgot-password",
            data: JSON.stringify(dataObject),
            contentType: "application/json",
            dataType: "application/json",
            statusCode: {
                200: function () {
                    setTimeout(function () {
                        btn.prop('disabled', false);
                    }, 1000);
                    $("#success").css("display", "block");
                    $("#email").val("");
                },
                400: function (data) {
                    var json = $.parseJSON(data.responseText);
                    setTimeout(function () {
                        btn.prop('disabled', false);
                    }, 1000);
                    $("#error>p").text(json.message);
                    $("#error").css("display", "block");
                    $("#email").val("");
                }
            }
        });
    });

    $("#resetPasswordButton").click(function () {
        var dataObject = {
            "token": $("#token").val(),
            "newPassword": $("#newPassword").val(),
            "confirmNewPassword": $("#confirmNewPassword").val()
        };
        var btn = $(this);
        btn.prop('disabled', true);
        $("#success").css("display", "none");
        $("#error").css("display", "none");
        $.ajax({
            type: "POST",
            url: "/api/reset-password",
            data: JSON.stringify(dataObject),
            contentType: "application/json",
            dataType: "application/json",
            statusCode: {
                200: function () {
                    setTimeout(function () {
                        btn.prop('disabled', false);
                    }, 1000);
                    $("#success").css("display", "block");
                    $("#newPassword").val("");
                    $("#confirmNewPassword").val("");
                },
                400: function (data) {
                    var json = $.parseJSON(data.responseText);
                    setTimeout(function () {
                        btn.prop('disabled', false);
                    }, 1000);
                    $("#error").css("display", "block");
                    $("#error>p").text(json.message);
                    $("#newPassword").val("");
                    $("#confirmNewPassword").val("");
                }
            }
        });
    });

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

    /*$('#email').on('input', function () {
        if ($.trim($(this).val()).length < 1 || $.trim($("#password").val()).length < 1) {
            /*$(this).css({
                "border-color": "red",
                "border-width": "3px",
                "border-style": "solid"
            });
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
            });
            $("#loginButton").prop("disabled", false);
            //$("#loginEmailErrorMessage").css("display", "none");
            $("#loginButton").css("cursor", "pointer");
        }

    });

    $('#password').on('input', function () {
        if ($.trim($(this).val()).length < 1 || $.trim($("#email").val()).length < 1) {
            /*$(this).css({
                "border-color": "red",
                "border-width": "3px",
                "border-style": "solid"
            });
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
            });
            $("#loginButton").prop("disabled", false);
            // $("#loginPasswordErrorMessage").css("display", "none");
            $("#loginButton").css("cursor", "pointer");
        }

    });*/
});