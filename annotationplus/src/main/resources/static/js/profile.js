$(document).ready(function () {
    $(".closeDialog").click(function() {
        $(".formContainer").css("display","none");
    });

    $(".close").click(function() {
        $(".formContainer").css("display","none");
    });

    var successMessage = $('<div class=\"notify top-left do-show\" id=\"successMessage\" data-notification-status=\"success\"></div>');

    function animateSuccess(message){
        $(successMessage).remove();
        $('body').append(successMessage).clone();
        $(successMessage).text(message);
        $(successMessage).css("display","flex");
        setTimeout(function() {
            $(successMessage).fadeOut("slow");
        }, 2500);
    }

    function validatePassword() {
        var password = $("#password").val();
        var newPassword = $("#newPassword").val();
        var confirmNewPassword = $("#confirmNewPassword").val();

        if(password.trim() === ""){
            $("#passwordError>strong").text("Invalid Current Password");
            $('#passwordError').fadeIn('fast', function(){
            });
            return false;
        }
        if(newPassword.trim() === "" || newPassword.trim().length < 5 || newPassword.trim().length > 50 ){
            $("#passwordError>strong").text("New Password must be between 5 and 50 characters");
            $('#passwordError').fadeIn('fast', function(){
            });
            return false;
        }
        if(confirmNewPassword.trim() !==  newPassword.trim()){
            $("#passwordError>strong").text("Confirm New Password must be the same as New Password");
            $('#passwordError').fadeIn('fast', function(){
            });
            return false;
        }
        return true;
    }

    function validateName() {
        var firstName = $("#name").val();
        var lastName = $("#lastName").val();

        if(/[^a-zA-Z ]/.test(firstName) || /[^a-zA-Z ]/.test(lastName)){
            $("#userError>strong").text("Names should be latin with no special characters and numbers");
            $('#userError').fadeIn('fast', function(){
            });
            return false;
        }

        if(firstName.trim() === "" || firstName.trim().length < 1 || firstName.trim().length > 25){
            $("#userError>strong").text("First Name must be between 1 and 25 characters");
            $('#userError').fadeIn('fast', function(){
            });
            return false;
        }
        if(lastName.trim() === "" || lastName.trim().length < 1 || lastName.trim().length > 25){
            $("#userError>strong").text("Last Name must be between 1 and 25 characters");
            $('#userError').fadeIn('fast', function(){
            });
            return false;
        }
        return true;
    }

    $("#passwordError").css("display","none");
    $("#closePasswordError").click(function() {
        $("#passwordError").css("display","none");
    });
    $("#userError").css("display","none");
    $("#closeNamesError").click(function() {
        $("#userError").css("display","none");
    });

    var firstNameValue;
    var lastNameValue;

    $.ajax({
        type: "GET",
        url: "/api/user",
        statusCode: {
            200: function (e) {
                $("#email").text(e.email);
                $("#name").val(e.name);
                $("#lastName").val(e.lastName);

                firstNameValue = e.name;
                lastNameValue = e.lastName;
            }
        }
    });

    $("#updatePassword").prop('disabled', true);

    $("#updateNames").prop('disabled', true);

    $('#name').on('input', function() {
        if($("#name").val() !== firstNameValue){
            $("#updateNames").prop('disabled', false);
        }
        if($("#name").val() === firstNameValue && $("#lastName").val() === lastNameValue){
            $("#updateNames").prop('disabled', true);
        }
    });

    $('#lastName').on('input', function() {
        if($("#lastName").val() !== lastNameValue){
            $("#updateNames").prop('disabled', false);
        }
        if($("#lastName").val() === lastNameValue && $("#name").val()=== firstNameValue){
            $("#updateNames").prop('disabled', true);
        }
    });

    $('#password').on('input', function() {
        if($(this).val().trim() === ""){
            $("#updatePassword").prop('disabled', true);
        }
        if($(this).val().trim() !== "" && $("#newPassword").val().trim() !== "" && $("#confirmNewPassword").val().trim() !== ""){
            $("#updatePassword").prop('disabled', false);
        }
    });

    $('#newPassword').on('input', function() {
        if($(this).val().trim() === ""){
            $("#updatePassword").prop('disabled', true);
        }
        if($(this).val().trim() !== "" && $("#password").val().trim() !== "" && $("#confirmNewPassword").val().trim() !== ""){
            $("#updatePassword").prop('disabled', false);
        }
    });

    $('#confirmNewPassword').on('input', function() {
        if($(this).val().trim() === ""){
            $("#updatePassword").prop('disabled', true);
        }
        if($(this).val().trim() !== "" && $("#password").val().trim() !== "" && $("#newPassword").val().trim() !== ""){
            $("#updatePassword").prop('disabled', false);
        }
    });

    $(window).bind('beforeunload', function() {
        if($("#name").val() !== firstNameValue || $("#lastName").val() !== lastNameValue || $("#password").val() !== "" || $("#confirmNewPassword").val() !== "" || $("#newPassword").val() !== ""){
            return "You have unsaved changes on this page. Do you want to leave this page and discard your changes or stay on this page?";
        }
    });

    $("#updateNames").click(function(e) {
        var validate = validateName();
        if (validate){
            e.preventDefault();
            $('#userError').fadeOut('fast', function () {});
            var btn = $(this);
            var dataObject = {
                "name": $("#name").val().trim(),
                "lastName": $("#lastName").val().trim()
            };
            $.ajax({
                type: "PUT",
                url: "/api/user",
                data: JSON.stringify(dataObject),
                contentType: "application/json",
                dataType: "application/json",
                statusCode: {
                    200: function () {
                        btn.prop('disabled', true);
                        firstNameValue = $("#name").val();
                        lastNameValue = $("#lastName").val();
                        animateSuccess("Name updated successfully");
                    },
                    400: function (e) {
                        var json = $.parseJSON(e.responseText);
                        btn.prop('disabled', false);
                        $("#userError>strong").text(json.message);
                        $('#userError').fadeIn('fast', function () {
                        });
                    }
                }
            });
        }
    });

    $("#updatePassword").click(function(e) {
        var validate =  validatePassword();
        if(validate){
            e.preventDefault();
            $('#passwordError').fadeOut('fast', function () {});
            var btn = $(this);
            var dataObject = {
                "password": $("#password").val(),
                "newPassword": $("#newPassword").val(),
                "confirmNewPassword": $("#confirmNewPassword").val()
            };
            $.ajax({
                type: "PUT",
                url: "/api/password",
                data: JSON.stringify(dataObject),
                contentType: "application/json",
                dataType: "application/json",
                statusCode: {
                    200: function () {
                        btn.prop('disabled', false);
                        $("#password").val("");
                        $("#newPassword").val("");
                        $("#confirmNewPassword").val("");
                        animateSuccess("Password updated successfully");
                    },
                    400: function (e) {
                        var json = $.parseJSON(e.responseText);
                        btn.prop('disabled', false);
                        $("#passwordError>strong").text(json.message);
                        $('#passwordError').fadeIn('fast', function () {
                        });
                    }
                }
            });
        }
    });
});