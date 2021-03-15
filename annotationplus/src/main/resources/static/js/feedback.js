$(document).ready(function () {
    $(window).bind('beforeunload', function () {
        if ($("#name").val().trim() !== "" || $("#message").val().trim() !== "") {
            return "You have unsaved changes on this page. Do you want to leave this page and discard your changes or stay on this page?";
        }
    });

    var successMessage = $('<div class=\"notify top-left do-show\" id=\"successMessage\" data-notification-status=\"success\"></div>');

    function animateSuccess(message){
        $(successMessage).remove();
        $('body').append(successMessage).clone();
        $(successMessage).text(message);
        $(successMessage).css("display","flex");
        setTimeout(function() {
            $(successMessage).fadeOut("slow");
        }, 2000);
    }

    $(".closeDialog").click(function() {
        $(".formContainer").css("display","none");
    });

    $(".close").click(function() {
        $(".formContainer").css("display","none");
    });

    var modal = $("#myModal");
    var span = $("#close");
    span.click(function () {
        modal.css("display", "none");
    });

    $('#message').on('input', function () {
        if ($(this).val().trim() === "") {
            $("#submit").prop('disabled', true);
        }
        if ($(this).val().trim() !== "" && $("#name").val().trim() !== "") {
            $("#submit").prop('disabled', false);
        }
    });

    $('#name').on('input', function () {
        if ($(this).val().trim() === "") {
            $("#submit").prop('disabled', true);
        }
        if ($(this).val().trim() !== "" && $("#message").val().trim() !== "") {
            $("#submit").prop('disabled', false);
        }
    });

    $("#submit").click(function (e) {
        e.preventDefault();
        var btn = $(this);
        var dataObject = {
            "message": $("#message").val(),
            "type": $("#type").val(),
            "name": $("#name").val(),
            "component": $("#component").val()
        };
        $.ajax({
            type: "POST",
            url: "/api/feedback",
            data: JSON.stringify(dataObject),
            contentType: "application/json",
            dataType: "application/json",
            statusCode: {
                201: function () {
                    $('#name').val("");
                    $('#message').val("");
                    $("#submit").prop('disabled', true);
                    animateSuccess("Feedback sent successfully");
                },
                400: function (e) {
                    var json = $.parseJSON(e.responseText);
                    btn.prop('disabled', false);
                    $("#userError>strong").text(json.message);
                    $('#userError').fadeIn('fast', function () {});
                }
            }
        });
    });
});