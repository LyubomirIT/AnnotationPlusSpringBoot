$(document).ready(function () {
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
        if(newPassword.trim() === "" || newPassword.trim().length < 1 || newPassword.trim().length > 50 ){
            $("#passwordError>strong").text("New Password must be between 1 and 50 characters");
            $('#passwordError').fadeIn('fast', function(){
            });
            return false;
        }
        if(confirmNewPassword.trim() !=  newPassword.trim()){
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
            $("#userError>strong").text("Names cannot contain special characters");
            $('#userError').fadeIn('fast', function(){
            });
            return false;
        }

        if(firstName.trim() === "" || firstName.trim().length < 1 || firstName.trim().length > 50){
            $("#userError>strong").text("First Name must be between 1 and 50 characters");
            $('#userError').fadeIn('fast', function(){
            });
            return false;
        }
        if(lastName.trim() === "" || lastName.trim().length < 1 || lastName.trim().length > 50){
            $("#userError>strong").text("Last Name must be between 1 and 50 characters");
            $('#userError').fadeIn('fast', function(){
            });
            return false;
        }
        return true;
    }

    $("#passwordError").css("display","none");
    $("#close1").click(function() {
        $("#passwordError").css("display","none");
    });
    $("#userError").css("display","none");
    $("#close2").click(function() {
        $("#userError").css("display","none");
    });

    var modal = $("#myModal");
    var span = $("#close");

    span.click(function() {
        modal.css("display", "none");
    });

    var value1;
    var value2;

    $.ajax({
        type: "GET",
        url: "/api/user",
        success: function(result)
        {
            $("#email").text(result.email);
            $("#name").val(result.name);
            $("#lastName").val(result.lastName);

            value1 = result.name;
            value2 = result.lastName;
        }
    });

    $("#submit2").prop('disabled', true);

    $("#submit").prop('disabled', true);

    $('#name').on('input', function(e) {
        if($("#name").val() != value1){
            $("#submit").prop('disabled', false);
        }
        if($("#name").val() == value1 && $("#lastName").val() == value2){
            $("#submit").prop('disabled', true);
        }
    });

    $('#lastName').on('input', function(e) {
        if($("#lastName").val() != value2){
            $("#submit").prop('disabled', false);
        }
        if($("#lastName").val() == value2 && $("#name").val()== value1){
            $("#submit").prop('disabled', true);
        }
    });

    $('#password').on('input', function(e) {
        if($(this).val().trim() == ""){
            $("#submit2").prop('disabled', true);
        }
        if($(this).val().trim() != "" && $("#newPassword").val().trim() != "" && $("#confirmNewPassword").val().trim() != ""){
            $("#submit2").prop('disabled', false);
        }
    });

    $('#newPassword').on('input', function(e) {
        if($(this).val().trim() == ""){
            $("#submit2").prop('disabled', true);
        }
        if($(this).val().trim() != "" && $("#password").val().trim() != "" && $("#confirmNewPassword").val().trim() != ""){
            $("#submit2").prop('disabled', false);
        }
    });

    $('#confirmNewPassword').on('input', function(e) {
        if($(this).val().trim() == ""){
            $("#submit2").prop('disabled', true);
        }
        if($(this).val().trim() != "" && $("#password").val().trim() != "" && $("#newPassword").val().trim() != ""){
            $("#submit2").prop('disabled', false);
        }
    });

    $(window).bind('beforeunload', function() {
        if($("#name").val() != value1 || $("#lastName").val() != value2 || $("#password").val() != "" || $("#confirmNewPassword").val() != "" || $("#newPassword").val() != ""){
            return "You have unsaved changes on this page. Do you want to leave this page and discard your changes or stay on this page?";
        }
    });

    $("#submit").click(function(e) {
        var validate = validateName();
        if (!validate){
            //do nothing
        } else {
            e.preventDefault();
            var btn = $(this);
            var dataObject = {"name": $("#name").val().trim(), "lastName": $("#lastName").val().trim()};
            btn.prop('disabled', true);
            $.ajax({
                type: "PUT",
                url: "/api/user",
                // data:{ "name": $("#name").val(), "lastName": $("#lastName").val() },
                data: JSON.stringify(dataObject),
                contentType: "application/json",
                dataType: "application/json",
                /*statusCode:{
              200:function () {
                  btn.prop('disabled', false);
                  alert("sucess");
              }
            },
            error:function (data) {
                var json = $.parseJSON(data.responseText);
                console.log(json);
                console.log(json.message);
                //alert(json);
                $(".isa_error>i").text(json.message);
                $(".isa_error").css("display", "block");
                btn.prop('disabled', false);
            }*/
                statusCode: {
                    200: function () {
                        //  setTimeout(function(){
                        btn.prop('disabled', true);
                        //}, 1000);
                        //modal.css("display", "block");
                        value1 = $("#name").val();
                        value2 = $("#lastName").val();
                        $('.trigger-btn').click();

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
        }
    });

    $( "#submit2" ).click(function(e) {
        var validate =  validatePassword();
        if(!validate){
            //do nothing
        } else {
            e.preventDefault();
            var btn = $(this);
            var dataObject = {
                "password": $("#password").val(),
                "newPassword": $("#newPassword").val(),
                "confirmNewPassword": $("#confirmNewPassword").val()
            };
            btn.prop('disabled', true);
            $.ajax({
                type: "PUT",
                url: "/api/password",
                // data:{ "name": $("#name").val(), "lastName": $("#lastName").val() },
                data: JSON.stringify(dataObject),
                contentType: "application/json",
                dataType: "application/json",
                /*statusCode:{
              200:function () {
                  btn.prop('disabled', false);
                  alert("sucess");
              }
            },
            error:function (data) {
                var json = $.parseJSON(data.responseText);
                console.log(json);
                console.log(json.message);
                //alert(json);
                $(".isa_error>i").text(json.message);
                $(".isa_error").css("display", "block");
                btn.prop('disabled', false);
            }*/
                statusCode: {
                    200: function () {
                        setTimeout(function () {
                            btn.prop('disabled', false);
                        }, 1000);
                        //modal.css("display", "block");
                        $('.trigger-btn').click();
                        $("#password").val("");
                        $("#newPassword").val("");
                        $("#confirmNewPassword").val("");

                    },
                    400: function (data) {
                        var json = $.parseJSON(data.responseText);
                        setTimeout(function () {
                            btn.prop('disabled', false);
                        }, 1000);
                        $("#passwordError>strong").text(json.message);
                        $('#passwordError').fadeIn('fast', function () {
                            $('#passwordError').delay(3000).fadeOut();
                        });
                    }
                }
            });
        }
    });
});