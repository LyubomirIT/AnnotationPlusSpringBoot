$(document).ready(function () {
    $(window).bind('beforeunload', function () {
        if ($("#name").val().trim() != "" || $("#message").val().trim() != "") {
            return "You have unsaved changes on this page. Do you want to leave this page and discard your changes or stay on this page?";
        }
    });

    var modal = $("#myModal");
    var span = $("#close");
    span.click(function () {
        modal.css("display", "none");
    });

    $('#message').on('input', function (e) {
        if ($(this).val().trim() == "") {
            $("#submit").prop('disabled', true);
        }
        if ($(this).val().trim() != "" && $("#name").val().trim() != "") {
            $("#submit").prop('disabled', false);
        }
    });

    $('#name').on('input', function (e) {
        if ($(this).val().trim() == "") {
            $("#submit").prop('disabled', true);
        }
        if ($(this).val().trim() != "" && $("#message").val().trim() != "") {
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
        //btn.prop('disabled', true);
        $.ajax({
            type: "POST",
            url: "/api/feedback",
            // data:{ "name": $("#name").val(), "lastName": $("#lastName").val() },
            data: JSON.stringify(dataObject),
            contentType: "application/json",
            dataType: "application/json",
            statusCode: {
                201: function () {
                    $('.trigger-btn').click();
                    $('#name').val("");
                    $('#message').val("");
                    $("#submit").prop('disabled', true);
                },
                400: function (data) {
                    var json = $.parseJSON(data.responseText);
                    setTimeout(function () {
                        btn.prop('disabled', false);
                    }, 1000);
                    $("#userError>strong").text(json.message);
                    $('#userError').fadeIn('fast', function () {
                        //$('.isa_error').delay(3000).fadeOut();
                    });
                }
            }
        });
    });
});