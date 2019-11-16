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
                400: function (e) {
                    var json = $.parseJSON(e.responseText);
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
                400: function (e) {
                    var json = $.parseJSON(e.responseText);
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
});