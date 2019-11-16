$(document).ready(function() {
    var url = new URL(window.location.href);
    var noteId = url.searchParams.get("id");
    var editMode = false;
    var annotationColor;
    var annotationCategoryId;
    var commentContainer = $("#commentContainer");
    var annotationsContainer = $("#annotationsContainer");
    var categoriesContainer = $("#categoriesContainer");
    var commentId;
    var annotationId = url.searchParams.get("annotation-id");
    var dataObject;
    var annotationCategoryName;
    var trash = $("<i class=\"fa fa-trash fa-1x delCategory\"></i>");
    var pen = $("<i class=\"fa fa-pen fa-1x editName\"></i>");
    var responseJson;

    var successMessage = $('<div class=\"notify top-left do-show\" id=\"successMessage\" data-notification-status=\"success\"></div>');

    function animateSuccess(message){
        $(successMessage).remove();
        $('body').append(successMessage).clone();
        $(successMessage).text(message);
    }

    $('#content').css("width",$('#content').width() - 320);
    $("body").removeClass("b1 b2");
    $("#content > div ").css("margin-top", "0px");
    $("#content > p").first().css("margin-top", "0px");

    $('[data-toggle="tooltip"]').tooltip();

    $(".buttonTooltip").click(function(){
        $('.tooltip').css("display","none");
    });

    if(annotationId != null){
        try{
            $([document.documentElement, document.body]).animate({
                scrollTop: $("#content [annotation-id=" + annotationId + "]").first().offset().top
            }, 1000);
        } catch (e) {
        }
    }

    $(document).ajaxSend(function(evt, request, settings) {
        if(settings.type.toLowerCase() === "post" || settings.type.toLowerCase() === "put" || settings.type.toLowerCase() === "delete" ){
            $("#desact > i").removeClass('fa fa-exclamation-triangle fa-2x');
            $("#desact > i").removeClass('fa fa-check-circle fa-2x').addClass('fa fa-spinner fa-2x fa-spin');
            $("#desact ").attr("data-original-title","Loading...");
        }
    }).ajaxComplete(function (evt, request, settings) {
        if(settings.type.toLowerCase() === "post" || settings.type.toLowerCase() === "put" || settings.type.toLowerCase() === "delete") {
            if (request.status !== 400 && request.status !== 200 && request.status !== 201 && request.status !== 404) {
                $("#desact  > i").removeClass('fa fa-spinner fa-2x fa-spin').addClass('fa fa-exclamation-triangle fa-2x');
                $("#desact ").attr("data-original-title", "There are unsaved changes from your last request");
            } else {
                $("#desact  > i").removeClass('fa fa-spinner fa-2x fa-spin').addClass('fa fa-check-circle fa-2x');
                $("#desact ").attr("data-original-title", "All changes saved");
            }
        }
    });

    String.prototype.trunc = String.prototype.trunc ||
        function (n) {
            return (this.length > n) ? this.substr(0, n - 1) + '...' : this;
        };

    $(document).on("click",".comment", function() {
        var parent = $(this).parent();
        //annotationId = e.target.getAttribute('annotation-id');
        annotationId = parent.attr('annotation-id');
        $([document.documentElement, document.body]).animate({
            scrollTop: $("#content [annotation-id=" + annotationId + "]").first().offset().top
        }, 500);

    });

    function convertUTCDateToLocalDate(date) {
        var newDate = new Date(date.getTime() + date.getTimezoneOffset() * 60 * 1000);
        var offset = date.getTimezoneOffset() / 60;
        var hours = date.getHours();
        newDate.setHours(hours - offset);
        return newDate;
    }

    function rgbToHex(color) {
        color = "" + color;
        if (!color || color.indexOf("rgb") < 0) {
            return;
        }
        if (color.charAt(0) == "#") {
            return color;
        }

        var nums = /(.*?)rgb\((\d+),\s*(\d+),\s*(\d+)\)/i.exec(color),
            r = parseInt(nums[2], 10).toString(16),
            g = parseInt(nums[3], 10).toString(16),
            b = parseInt(nums[4], 10).toString(16);

        return "" + (
            (r.length == 1 ? "0" + r : r) +
            (g.length == 1 ? "0" + g : g) +
            (b.length == 1 ? "0" + b : b)
        );
    }

    function hoverdiv(e, divid) {

        var left = e.clientX + "px";
        var top = e.clientY + "px";

        var div = document.getElementById(divid);

        div.style.left = left;
        div.style.top = top;

        $("#" + divid).css("display", "block");
        return false;
    }

    $("#createCategory").click(function () {
           dataObject = {
            "name": $("#categoryNameField").val(),
            "noteId": noteId
        };
        /*setTimeout(function () {
            $("#createCategory").prop('disabled', true);
        }, 1000);*/
        $.ajax({
            type: "POST",
            url: "/api/annotationCategory",
            data: JSON.stringify(dataObject),
            contentType: "application/json",
            dataType: "application/json",
            statusCode: {
                201: function () {
                    listAnnotationCategories();
                    $('.formContainer').css("display","none");
                    $('#categoryNameField').val("");
                    animateSuccess("Annotation category successfully created")
                },
            400: function (e) {
                responseJson = $.parseJSON(e.responseText);
                setTimeout(function () {
                    $("#createCategory").prop('disabled', false);
                }, 1000);
                $("#createCategoryErrorMessage").text(responseJson.message);
                $('#createCategoryErrorMessage').fadeIn('fast', function () {
                });
            }
        }
        });
    });

    $("#updateCategory").click(function () {
        dataObject = {
            "name": $("#updateCategoryNameField").val()
        };
        $.ajax({
            type: "PUT",
            url: "/api/annotationCategory/"+ annotationCategoryId,
            data: JSON.stringify(dataObject),
            contentType: "application/json",
            dataType: "application/json",
            statusCode: {
                200: function (e) {
                    responseJson = $.parseJSON(e.responseText);
                    $(".categoryName.selected").contents().first()[0].textContent = responseJson.name;
                    $('.formContainer').css("display","none");
                    $('#updateCategoryNameField').val("");
                    animateSuccess("Annotation category successfully updated")
                },
                400: function (e) {
                    responseJson = $.parseJSON(e.responseText);
                    setTimeout(function () {
                        $("#updateCategory").prop('disabled', false);
                    }, 1000);
                    $("#updateCategoryErrorMessage").text(responseJson.message);
                    $('#updateCategoryErrorMessage').fadeIn('fast', function () {
                    });
                }
            }
        });
    });

    $("#newCategory").click(function () {
        $("#createCategoryContainer").show();
    });

    $(document).on("click",".editName", function() {
        annotationCategoryName = $(this).parent().text().trim();
        $("#updateCategoryNameField").val(annotationCategoryName);
        $("#updateCategoryContainer").show();
    });

    $("#cancelCategory").click(function () {
        $(".formContainer").css("display", "none");
        $("#createCategoryErrorMessage").css("display", "none");
        $("#categoryNameField").val("");
    });

    $("#cancelUpdateCategory").click(function () {
        $(".formContainer").css("display", "none");
        $("#updateCategoryErrorMessage").css("display", "none");
        $("#updateCategoryNameField").val("");
    });

    $("#cancelDelete").click(function () {
        $(".formContainer").css("display", "none");
    });

    $("#closeCategoryDialog").click(function () {
        $(".formContainer").css("display", "none");
    });

    $("#cancelSessionMessage").click(function () {
        $(".formContainer").css("display", "none");
    });

    $("#closeSessionDialog").click(function () {
        $(".formContainer").css("display", "none");
    });

    $("#closeInfo").click(function () {
        $("#info").css("display", "none");
    });

    function listAnnotationCategories(){
    $.ajax({
        type: "GET",
        url: "/api/annotationCategory",
        data: {
            noteId: noteId
        },
        statusCode: {
            200: function (e) {
                try {
                    if (e.length > 0) {
                        $('#categoriesContainer li').slice(2).remove();
                        $("#categoriesMessage").text("");
                        for (var i = 0; i < e.length; i++) {
                            var annotationCategory = e[i];
                            var li = $("<li class='categoriesList'></li>");
                            var name = $("<div class='categoryName'><div>");
                            name.text(annotationCategory.name);
                            name.attr('id', annotationCategory.id);
                            li.css('height', '30px');
                            var div = $("<div class='rectangle'></div>");
                            div.append(name);
                            li.append(div);
                            $("#categoriesContainer").append(li);
                        }
                        $(".categoryName").removeClass('selected');
                        $(".categoryName:first").removeClass('categoryName').addClass('categoryName selected');
                        $(".categoryName:first").append(trash);
                        $(".categoryName:first").append(pen);
                        annotationCategoryId = $(".selected").attr('id');
                        $("#highlighter").attr("data-original-title", "Annotate using highlighter");
                        $("#highlighter").removeClass('btn btn-primary buttonTooltip disable').addClass('btn btn-primary buttonTooltip');
                        listAnnotationsByCategory();
                    } else {
                        $('#categoriesContainer li').slice(2).remove();
                        $("#categoriesMessage").text("No Created Categories");
                        $("#highlighter").removeClass('btn btn-primary buttonTooltip').addClass('btn btn-primary buttonTooltip disable');
                        $("#highlighter").attr("data-original-title", "Create a category before annotating !");
                    }
                } catch (e) {
                    $('#categoriesContainer li').slice(2).remove();
                    $("#categoriesMessage").text("Unable to get categories");
                }
            }
        }
    });
}

listAnnotationCategories();

    function listAnnotationsByCategory(){
        $.ajax({
            type: "GET",
            url: "/api/annotation",
            data: {
                annotationCategoryId: annotationCategoryId
            },
            statusCode: {
                200: function (e) {
                    try {
                        if (e.length > 0) {
                            $('#annotationsContainer li').slice(2).remove();
                            $("#annotationsMessage").text("");
                            for (var i = 0; i < e.length; i++) {
                                var annotation = e[i];
                                var dateCreated = convertUTCDateToLocalDate(new Date(annotation.createdTs));
                                var li = $("<li class='annotationsList'></li>");
                                var date = $("<div class='date'><div>").text(dateCreated.toLocaleString());
                                var content = $("<div class='comment'><div>").text(annotation.content.trunc(150));
                                var div = $("<div class='rectangle' annotation-id=" + "'" + annotation.id + "'" + "></div>");
                                div.attr("category-id", annotationCategoryId);
                                li.css('height', '30px');
                                div.append(date);
                                div.append(content);
                                div.css("background-color", annotation.color);
                                li.append(div);
                                $("#annotationsContainer").append(li);
                            }
                        } else {
                            $('#annotationsContainer li').slice(2).remove();
                            $("#annotationsMessage").text("No Annotations For This Category");
                        }
                    }catch (e) {
                        $('#annotationsContainer li').slice(2).remove();
                        $("#annotationsMessage").text("Unable to get annotations");
                    }
                }
            }
        });
    }

    $(document).on("click",".categoryName", function(e) {
        if (e.target.getAttribute('class') === "categoryName") {
            annotationCategoryId = e.target.getAttribute('id');
            $(".editName").remove();
            $(".delCategory").remove();
            $(".categoryName").removeClass('selected');
            $(".categoryName").css("background-color", "");
            $(e.target).css("background-color", "black");
            $(e.target).addClass('selected');
            var trash = $("<i class=\"fa fa-trash fa-1x delCategory\"></i>");
            var pen = $("<i class=\"fa fa-pen fa-1x editName\"></i>");
            $(e.target).append(trash);
            $(e.target).append(pen);
            listAnnotationsByCategory();
        }
    });

    $("#content").click(function(event) {
        $("#commentBlock").css("display","none");
        $("#changeColorForm").css("display","none");
        if (event.target.getAttribute('annotation-id')) {
            annotationId = event.target.getAttribute('annotation-id');
            $.ajax({
                type: "GET",
                url: "/api/annotation/" + annotationId,
                statusCode: {
                    200: function (e) {
                        $("#info").css("display", "none");
                        $("#username").text("By " + e.username);
                        hoverdiv(event, "info");
                    }
                }
            });
        } else{
            $("#info").css("display","none");
        }
    });

    $("#deleteAnnotation").click(function() {

        $.ajax({
            type: "DELETE",
            url: "/api/annotation/" + annotationId,
            statusCode: {
                200: function () {
                    $(".rectangle[annotation-id =" + annotationId + "]").closest(".annotationsList").remove();
                    if ($('.annotationsList').length < 1) {
                        $("#annotationsMessage").text("No Annotations For This Category");
                    }
                    $("[annotation-id =" + annotationId + "]").css("background-color", "");
                    $("[annotation-id =" + annotationId + "]").removeAttr("category-id");
                    $("[annotation-id =" + annotationId + "]").removeAttr("annotation-id");
                    $("#info").css("display", "none");
                    $('#commentContainer li').slice(2).remove();
                    $("#commentsMessage").text("Please click on a annotation !");

                    dataObject = {"content": $("#content").html()};

                    $.ajax({
                        type: "PUT",
                        url: "/api/note/" + noteId,
                        data: JSON.stringify(dataObject),
                        contentType: "application/json",
                        dataType: "application/json",
                        statusCode: {
                            200: function () {
                                animateSuccess("Annotation successfully deleted")
                            }
                        }
                    });
                }
            }
        });
    });

    $("#comment").click(function(e) {
        hoverdiv(e,"commentBlock");
    });

    $('#update').click(function(){
        $("[annotation-id =" +  annotationId + "]").css("background-color", '#' + $("#jscolor").val());
        annotationColor = $("#jscolor").val();
        dataObject = {
            "color":'#' + annotationColor.toUpperCase()
        };
        $.ajax({
            type: "PUT",
            url: "/api/annotation/" + annotationId,
            data: JSON.stringify(dataObject),
            contentType: "application/json",
            dataType: "application/json",
            statusCode: {
                200: function () {
                    dataObject = {"content": $("#content").html()};

                    $.ajax({
                        type: "PUT",
                        url: "/api/note/" + noteId ,
                        data: JSON.stringify(dataObject),
                        contentType: "application/json",
                        dataType: "application/json",
                        statusCode: {
                            200: function () {
                            }
                        }
                    });
                    listAnnotationsByCategory();
                }
            }
        });
    });

    $("#pencil").click(function(e) {
        var jscolor = $('#jscolor');
        jscolor.val(rgbToHex($("[annotation-id =" +  annotationId + "]").css("background-color")).toUpperCase());
        hoverdiv(e,"changeColorForm");
        document.getElementById("jscolor").jscolor.show();
    });

    $("#cancel").click(function() {
       $("#changeColorForm").css("display","none");
    });

    $("#changeColorForm > div").click(function() {
        document.getElementById('jscolor').jscolor.show();
    });

    $("#categoryNameField").on('input', function() {
        if($(this).val().trim() == ""){
            $("#createCategory").prop('disabled', true);
            $("#createCategoryErrorMessage").text("Category Name cannot be empty !");
            $('#createCategoryErrorMessage').fadeIn('fast', function(){
            });
        }
        else if($(this).val().trim() != "" && $(this).val().trim().length < 51){
            $("#createCategory").prop('disabled', false);
        }
        else if($(this).val().trim().length > 50){
            $("#createCategory").prop('disabled', true);
            $("#createCategoryErrorMessage").text("Category Name cannot be longer than 50 characters !");
            $('#createCategoryErrorMessage').fadeIn('fast', function(){
            });
        }
    });

    $("#updateCategoryNameField").on('input', function() {
        if($(this).val().trim() == ""){
            $("#updateCategory").prop('disabled', true);
            $("#updateCategoryErrorMessage").text("Category Name cannot be empty !");
            $('#updateCategoryErrorMessage').fadeIn('fast', function(){
            });
        }
        else if($(this).val().trim() === annotationCategoryName) {
            $("#updateCategoryErrorMessage").text("");
            $("#updateCategory").prop('disabled', true);
        }
        else if($(this).val().trim() != "" && $(this).val().trim().length < 51){
            $("#updateCategory").prop('disabled', false);
        }
        else if($(this).val().trim().length > 50){
            $("#updateCategory").prop('disabled', true);
            $("#updateCategoryErrorMessage").text("Category Name cannot be longer than 50 characters !");
            $('#updateCategoryErrorMessage').fadeIn('fast', function(){
            });
        }
    });

    $("#toggleComments").click(function() {
        if($("#sidebar-wrapper").width() === 0){
            $('#content').css("width",$('#content').width() -320);
            $('.btn-toolbar').css("padding-right","320px");
        }
        annotationsContainer.css("display","none");
        categoriesContainer.css("display","none");
        commentContainer.css("display","block");
        $(this).css("right","570px");
        $(this).css("margin-right","-250px");
        $(this).css("background-color","lightgray");
        $("#toggleComments > i").css("color","black");
        $("#toggleAnnotations").css("right","570px");
        $("#toggleAnnotations").css("margin-right","-250px");
        $("#sidebar-wrapper").css("width","320px");
        $("#toggleAnnotations").css("background-color","#6C6C6C");
        $("#toggleAnnotations > i").css("color","#fff");
    });

    $("#toggleAnnotations").click(function() {
        if($("#sidebar-wrapper").width() === 0){
            $('#content').css("width",$('#content').width() -320);
            $('.btn-toolbar').css("padding-right","320px");
        }
        commentContainer.css("display","none");
        annotationsContainer.css("display","block");
        categoriesContainer.css("display","block");
        $(this).css("right","570px");
        $(this).css("margin-right","-250px");
        $(this).css("background-color","lightgray");
        $("#toggleAnnotations > i").css("color","black");
        $("#toggleComments").css("right","570px");
        $("#toggleComments").css("margin-right","-250px");
        $("#sidebar-wrapper").css("width","320px");
        $("#toggleComments").css("background-color","#6C6C6C");
        $("#toggleComments > i").css("color","#fff");
    });

    $("#colors span").click(function() {
         annotationColor = $(this).css("background-color");
            editMode = true;
         $("#highlighter").attr("data-original-title","Disable annotation");
            $("#colors").css("display","none");
            switch (annotationColor) {
                default:
                    $("body").css('cursor', 'url(../cursors/highlight_FFCC99.cur), auto');
            }
    });

    $("#highlighter").click(function() {
        if ($(this).attr("class").indexOf("disable") > -1) {
        } else {
            $("body").css('cursor', 'default');
        if (!editMode) {
            if ($("#colors").css("display") == 'block') {
                $("#colors").css("display", "none");
            } else {
                $("#colors").css("display", "block");
            }
        } else {
            editMode = false;
            $("#colors").css("display", "none");
            $("#highlighter").attr("data-original-title","Annotate using highlighter");
        }
    }
    });

    $("#slide-submenu").click(function() {
        $("#sidebar-wrapper").css("width","0px");
        $("#toggleComments").css("right","0");
        $("#toggleComments").css("margin-right","0px");
        $("#toggleAnnotations").css("right","0");
        $("#toggleAnnotations").css("margin-right","0px");
        $("#toggleComments").css("background-color","#6C6C6C");
        $("#toggleComments > i").css("color","#fff");
        $("#toggleAnnotations").css("background-color","#6C6C6C");
        $("#toggleAnnotations > i").css("color","#fff");
        $('#content').css("width",$('#content').width() + 320);
        $('.btn-toolbar').css("padding-right","0px");
    });

    $("#cancelComment").click(function() {
        $("#commentBlock").css("display","none");
        $('textarea').val("");
    });

    $("#info").click(function (e) {
        var target = $(e.target)[0];
        if (target != $("#pencil")[0] && target != $("#pencil > i")[0]) {
            $("#changeColorForm").css("display", "none");
        }
        if(target != $("#comment")[0] && target != $("#comment > i")[0]){
            $("#commentBlock").css("display", "none");
        }
    });

    $("#saveComment").click(function() {
          dataObject = {
            "comment": $("#commentArea").val(),
            "annotationId": annotationId,
            "noteId": noteId
        };
        $.ajax({
            type: "POST",
            url: "/api/comment",
            data: JSON.stringify(dataObject),
            contentType: "application/json",
            dataType: "application/json",
            statusCode: {
                201: function (e) {
                    var responseJson = $.parseJSON(e.responseText);
                    var date = convertUTCDateToLocalDate(new Date(responseJson.createdTs));
                    $("#commentBlock").css("display","none");
                    $('textarea').val("");
                    $('#commentsMessage').text("");
                    var li = $("<li class='commentList'></li>");
                    var email = $("<div class='email'><div>").text(responseJson.userName);
                    var date = $("<div class='date'><div>").text(date.toLocaleString());
                    var comment = $("<div class='comment'><div>").text(responseJson.comment);
                    var div = $("<div class='rectangle' annotation-id=" + "'" + responseJson.annotationId + "'" + "></div>");
                    div.attr("category-id", annotationCategoryId);
                    var trash = $("<span class='deleteComment'><i class='fa fa-trash fa-2x' aria-hidden='true'></i></span>");
                    trash.attr("id",responseJson.id);
                    div.append(trash);
                    div.append(email);
                    div.append(date);
                    div.append(comment);
                    div.css("background-color",$("[annotation-id =" +  annotationId + "]").css("background-color"));
                    li.append(div);
                    $("#commentContainer li:nth-child(2)").after(li);
                }
            }
        });
    });

        $(function () {
            $("#content").bind('mouseup', function () {
                if(editMode){
                    color(annotationColor);
                }
            });
        });

        function color(color) {
            var select = getSelected();
            if (/\S/.test(select)) {
              dataObject = {
                "noteId": noteId,
                "annotationCategoryId": annotationCategoryId,
                "content": select.toString(),
                "color": '#' + rgbToHex(color).toUpperCase()
            };
            $.ajax({
                type: "POST",
                url: "/api/annotation",
                data: JSON.stringify(dataObject),
                contentType: "application/json",
                dataType: "application/json",
                statusCode: {
                    201: function (e) {
                        var responseJson = $.parseJSON(e.responseText);
                        annotationId = responseJson.id;
                            var sel = window.getSelection();
                            if (sel.rangeCount && sel.getRangeAt) {
                                var range = sel.getRangeAt(0);
                            }
                            document.designMode = "on";
                            if (range) {
                                sel.removeAllRanges();
                                sel.addRange(range);
                            }
                            document.execCommand("BackColor", false, color);
                            document.designMode = "off";

                            //setTimeout(function () {
                                if (range.commonAncestorContainer.nodeType === 3) {
                                    console.log("nodeType 3 annotationId: " + annotationId);
                                    console.log("nodeType " + range.commonAncestorContainer.nodeType);
                                    var span = sel.anchorNode.parentNode;
                                    span.setAttribute("annotation-id", annotationId);
                                    span.setAttribute("category-id", annotationCategoryId);
                                } else if (range.commonAncestorContainer.nodeType === 1) {
                                    console.log("nodeType " + range.commonAncestorContainer.nodeType);
                                     range.commonAncestorContainer.querySelectorAll('span').forEach(function (e) {
                                        if (sel.containsNode(e, true)) {
                                            if (e.style.backgroundColor === color) {
                                                console.log(e.style.backgroundColor);
                                                console.log(e);
                                                console.log("nodeType 1 annotationId: " + annotationId);
                                                e.setAttribute("annotation-id", annotationId);
                                                e.setAttribute("category-id", annotationCategoryId);
                                            }
                                        } else {
                                        }
                                    });
                                }
                            //}, 200);

                        dataObject = {"content": $("#content").html()};
                        setTimeout(function () {
                        $.ajax({
                            type: "PUT",
                            url: "/api/note/" + noteId,
                            data: JSON.stringify(dataObject),
                            contentType: "application/json",
                            dataType: "application/json",
                            statusCode: {
                                200: function () {
                                }
                            }
                        });
                        }, 300);
                        listAnnotationsByCategory();
                    },
                    400: function () {
                    }
                }
            });
            }
        }

    $(document).on("click",".fa-2x", function(e) {
        commentId = $(e.target).parent().attr("id");
        $.ajax({
            type: "DELETE",
            url: "/api/comment/" + commentId,
            contentType: "application/json",
            dataType: "application/json",
            statusCode: {
                200: function () {
                    $(e.target).closest(".commentList").remove();
                    if($('.commentList').length < 1){
                        $("#commentsMessage").text("No comments for this annotation");
                        animateSuccess("Comment successfully deleted");
                    }
                }
            }
        });
    });

    $(document).on("click",".delCategory", function(e) {
        annotationCategoryId = $(e.target).parent().attr("id");
        $("#deleteCategoryContainer").css("display","block");
    });

    $("#confirmDelete").click(function() {
        //$(this).prop("disabled",true);
        $.ajax({
                type: "DELETE",
                url: "/api/annotationCategory/" + annotationCategoryId,
                contentType: "application/json",
                dataType: "application/json",
                statusCode: {
                    200: function () {
                        $('#commentContainer li').slice(2).remove();
                        $('#annotationsContainer li').slice(2).remove();
                        $("#commentsMessage").text("Please click on a annotation !");
                        $("#" +  annotationCategoryId + "").closest(".categoriesList").remove();
                        $("[category-id =" +  annotationCategoryId + "]").css("background-color", "");
                        $("[category-id =" +  annotationCategoryId + "]").removeAttr("annotation-id");
                        $("[category-id =" +  annotationCategoryId + "]").removeAttr("category-id");
                        $("#deleteCategoryContainer").css("display","none");
                        $("#confirmDelete").prop("disabled",false);
                        if($('.categoryName').length < 1){
                            $("#categoriesMessage").text("No Created Categories");
                            $("#annotationsMessage").text("Please select a category");
                            $(".categoriesList").remove();
                            $("#highlighter").removeClass('btn btn-primary buttonTooltip').addClass('btn btn-primary buttonTooltip disable');
                            $("#highlighter").attr("data-original-title","Create a category before annotating !");
                            $("body").css('cursor', 'default');
                            editMode = false;
                        } else {
                            $(".categoryName:first").removeClass('categoryName').addClass('categoryName selected');
                            $(".categoryName:first").append(trash);
                            $(".categoryName:first").append(pen);
                            annotationCategoryId = $(".selected").attr('id');
                            listAnnotationsByCategory();
                        }
                        dataObject = {"content": $("#content").html()};
                        $.ajax({
                            type: "PUT",
                            url: "/api/note/" + noteId ,
                            data: JSON.stringify(dataObject),
                            contentType: "application/json",
                            dataType: "application/json",
                            statusCode: {
                                200: function () {
                                    animateSuccess("Annotation category successfully deleted");
                                }
                            }
                        });
                    }
                }
        });
    });

    $("#content").click(function(e) {
            if (e.target.getAttribute('annotation-id')) {
                var attribute = e.target.getAttribute('annotation-id');
                $.ajax({
                    type: "GET",
                    url: "/api/comment",
                    data: {
                        annotationId: attribute
                    },
                    statusCode: {
                        200: function (e) {
                            try {
                                if (e.length > 0) {
                                    $('#commentContainer li').slice(2).remove();
                                    $('#commentsMessage').text("");
                                    $("#message").text("");
                                    for (var i = 0; i < e.length; i++) {
                                        var comment = e[i];
                                        var dateCreated = convertUTCDateToLocalDate(new Date(comment.createdTs));
                                        var li = $("<li class='commentList'></li>");
                                        var email = $("<div class='email'><div>").text(comment.userName);
                                        var date = $("<div class='date'><div>").text(dateCreated.toLocaleString());
                                        var commentDiv = $("<div class='comment'><div>").text(comment.comment);
                                        var div = $("<div class='rectangle' annotation-id=" + "'" + attribute + "'" + "></div>");
                                        var trash = $("<span class='deleteComment'><i class='fa fa-trash fa-2x' aria-hidden='true'></i></span>");
                                        trash.attr("id", comment.id);
                                        div.append(trash);
                                        div.append(email);
                                        div.append(date);
                                        div.append(commentDiv);
                                        div.css("background-color", $("[annotation-id =" + annotationId + "]").css("background-color"));
                                        li.append(div);
                                        commentContainer.append(li);
                                    }
                                } else {
                                    $('#commentContainer li').slice(2).remove();
                                    $("#commentsMessage").text("No comments for this annotation");
                                }
                            } catch (e) {
                                $('#commentContainer li').slice(2).remove();
                                $("#commentsMessage").text("An error occurred while getting comments");
                            }
                        }
                    }
                });
            }
        });

        function getSelected() {
            if (window.getSelection) {
                return window.getSelection();
            } else if (document.getSelection) {
                return document.getSelection();
            } else {
                var selection = document.selection && document.selection.createRange();
                if (selection.text) {
                    return selection.text;
                }
                return false;
            }
            return false;
        }
});
