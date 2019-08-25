$(document).ready(function() {
    $("body").removeClass("b1 b2");
    $("#content > div ").css("margin-top", "0px");

    $('[data-toggle="tooltip"]').tooltip();

    $(".buttonTooltip").click(function(){
        $('.tooltip').css("display","none");
    });

    $(document)
        .ajaxStart(function () {
            $("#desact > i").removeClass('fa fa-check-circle fa-2x').addClass('fa fa-spinner fa-2x fa-spin');
            $("#desact ").attr("data-original-title","Saving...");
        })
        .ajaxStop(function () {
            $("#desact  > i").removeClass('fa fa-spinner fa-2x fa-spin').addClass('fa fa-check-circle fa-2x');
            $("#desact ").attr("data-original-title","All changes saved");
        });

    String.prototype.trunc = String.prototype.trunc ||
        function (n) {
            return (this.length > n) ? this.substr(0, n - 1) + '...' : this;
        };

    var url = new URL(window.location.href);
    var noteId = url.searchParams.get("id");
    var editMode = false;
    var annotationColor;
    var annotationCategoryId;
    var commentContainer = $("#commentContainer");
    var annotationsContainer = $("#annotationsContainer");
    var categoriesContainer = $("#categoriesContainer");
    var commentId;


    function makeid(length) {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        for (var i = 0; i < length; i++)
            text += possible.charAt(Math.floor(Math.random() * possible.length));

        return text;
    }

    var title;

    function uuidv4() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }

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

    var annotationId;

    function hoverdiv(e, divid) {

        var left = e.clientX + "px";
        var top = e.clientY + "px";

        var div = document.getElementById(divid);

        div.style.left = left;
        div.style.top = top;

        // $("#"+divid).append("<i class=\"fa fa-trash\" id=\"deleteAnnotation\" aria-hidden=\"true\"></i>");
        $("#" + divid).css("display", "block");
        return false;
    }

    $("#createCategory").click(function () {
        var dataObject = {
            "name": $("#categoryNameField").val(),
            "noteId": noteId
        };
        setTimeout(function () {
            $("#createCategory").prop('disabled', true);
        }, 1000);
        $.ajax({
            type: "POST",
            url: "/api/annotationCategory",
            data: JSON.stringify(dataObject),
            contentType: "application/json",
            dataType: "application/json",
            statusCode: {
                201: function () {
                    console.log("Category Created Successfully");
                    listAnnotationCategories();
                    $('.container').css("display","none");
                    $('#categoryNameField').val("");
                },
            400: function (e) {
                var json = $.parseJSON(e.responseText);
                setTimeout(function () {
                    $("#createCategory").prop('disabled', false);
                }, 1000);
                $("#createCategoryErrorMessage").text(json.message);
                $('#createCategoryErrorMessage').fadeIn('fast', function () {
                });
            }
        }
        });
    });

    $("#newCategory").click(function (e) {
        //$(".container").css("display", "block");
        $(".container").show();
    });

    $("#cancelCategory").click(function (e) {
        $(".container").css("display", "none");
        $("#createCategoryErrorMessage").css("display", "none");
        $("#categoryNameField").val("");
    });


    $(".rectangle").click(function (e) {
        console.log("entered click method");
        annotationId = e.target.getAttribute('annotation-id');
        console.log("annotationid: " + annotationId);
        console.log(e.target);
        $([document.documentElement, document.body]).animate({
            scrollTop: $("#content [annotation-id=" + annotationId + "]")[0].offset().top
        }, 2000);

    });

    function listAnnotationCategories(){
    $.ajax({
        type: "GET",
        url: "/api/annotationCategory/" + noteId,
        success: function (result) {
            if (result.length > 0) {
                //$('.container').css("display","none");
                $('#categoriesContainer li').slice(2).remove();
                $("#categoriesMessage").text("");
                for (var i = 0; i < result.length; i++) {
                    var o = result[i];
                    var li = $("<li class='categoriesList'></li>");
                    // var li = $(".categoriesList");
                    var name = $("<div class='categoryName'><div>");
                    name.text(o.name);
                    name.attr('id', o.id);
                    name.css('text-align', 'center');
                    li.css('height', '30px');
                    var div = $("<div class='rectangle'></div>");
                    div.append(name);
                    li.append(div);
                    $("#categoriesContainer").append(li);
                }
                $(".categoryName").removeClass('selected');
                $(".categoryName:first").removeClass('categoryName').addClass('categoryName selected');
                annotationCategoryId = $(".selected").attr('id');
                $("#highlighter").attr("data-original-title","Annotate using highlighter");
                $("#highlighter").removeClass('btn btn-primary buttonTooltip disable').addClass('btn btn-primary buttonTooltip');
                listAnnotationsByCategory();

            } else {
                $('#categoriesContainer li').slice(2).remove();
                $("#categoriesMessage").text("No Created Categories");
                $("#highlighter").removeClass('btn btn-primary buttonTooltip').addClass('btn btn-primary buttonTooltip disable');
                $("#highlighter").attr("data-original-title","Create a category before annotating !");
            }
        }
    });
}

listAnnotationCategories();


    function listAnnotationsByCategory(){
        $.ajax({
            type: "GET",
            url: "/api/annotation/category=" + annotationCategoryId,
            success: function (result) {
                if (result.length > 0) {
                    //$(".rectangle").remove();
                    $('#annotationsContainer li').slice(2).remove();
                    // $('li').not(':first').remove();
                    $("#annotationsMessage").text("");
                    //var date;
                    for (var i = 0; i < result.length; i++) {
                        var o = result[i];
                        var datee = convertUTCDateToLocalDate(new Date(o.createdTs));
                        var li = $("<li class='annotationsList'></li>");
                        var date = $("<div class='date'><div>").text(datee.toLocaleString());
                        var content = $("<div class='comment'><div>").text(o.content.trunc(150));
                        var div = $("<div class='rectangle' annotation-id=" + "'" + o.annotationId + "'" + "></div>");
                        li.css('height', '30px');
                        div.append(date);
                        div.append(content);
                        div.css("background-color", o.color);
                        li.append(div);
                        $("#annotationsContainer").append(li);
                        //$("#commentContainer li:first-child").after(li);
                    }
                } else {
                    $('#annotationsContainer li').slice(2).remove();
                    $("#annotationsMessage").text("No Annotations For This Category");
                }
            }
        });
    }

    $("body").click(function(e) {
        if (e.target.getAttribute('class') == "categoryName") {
             annotationCategoryId = e.target.getAttribute('id');
            $(".categoryName").removeClass('selected');
            $(".categoryName").css("background-color","");
            $(e.target).css("background-color","black");
            $(e.target).addClass('selected');
            listAnnotationsByCategory();
        } else {
            //do nothing
        }
    });

    $("#content").click(function(e) {
        $("#commentBlock").css("display","none");
        $("#changeColorForm").css("display","none");
        if (e.target.getAttribute('annotation-id')) {
            annotationId = e.target.getAttribute('annotation-id');
            console.log("Entered click method");
            $.ajax({
                type: "GET",
                url: "/api/annotation/" + annotationId,
                success: function(result)
                {
                    $("#info").css("display","none");
                    //$("#info").text("by " + result.username);
                    //$("#info").contents().filter(function(){ return this.nodeType == 3; }).first().replaceWith("by " + result.username);
                    $("#username").text("By " + result.username);
                    //$("#info").text("By " + result.username);
                    //$("#info").append("<span id =\"comment\"><i class=\"fa fa-comment fa-2x\" aria-hidden=\"true\"></i></span><span id =\"deleteAnnotation\"><i class=\"fa fa-trash fa-2x\" aria-hidden=\"true\"></i></span>");
                    hoverdiv(e,"info");
                    //$(".fa-pen-square").css("color",$("[annotation-id =" +  annotationId + "]").css("background-color"));
                }
            });
        } else{
            $("#info").css("display","none");
        }
    });

    $("#deleteAnnotation").click(function(e) {

        $.ajax({
            type: "DELETE",
            url: "/api/annotation/" + annotationId,
            success: function()
            {
                $(".rectangle[annotation-id =" +  annotationId + "]").closest(".annotationsList").remove();
                if($('.annotationsList').length < 1 ){
                    $("#annotationsMessage").text("No Annotations For This Category");
                }
                $("[annotation-id =" +  annotationId + "]").css("background-color", "");
                $("[annotation-id =" +  annotationId + "]").removeAttr("annotation-id");
                $("#info").css("display","none");
                $('#commentContainer li').slice(2).remove();
                $("#commentsMessage").text("Please click on a annotation !");
                //listAnnotationsByCategory();

                var dataObject = {"content": $("#content").html()};

                $.ajax({
                    type: "PUT",
                    url: "/api/notes/" + noteId ,
                    data: JSON.stringify(dataObject),
                    contentType: "application/json",
                    dataType: "application/json",
                    statusCode: {
                        200: function (e) {
                            console.log("Update Successfull")
                        }
                    }
                });
            }
        });
    });

    $("#comment").click(function(e) {
       // $("#info").css("display","none");
        hoverdiv(e,"commentBlock");
    });

    var contains = $('span:contains("Cancel")');

    $('#update').click(function(){
        $("[annotation-id =" +  annotationId + "]").css("background-color", '#' + $("#jscolor").val());
        title = makeid(10);
        annotationColor = $("#jscolor").val();
        console.log(annotationColor);

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
                200: function (e) {
                    console.log("Annotation Updated Successfully");
                    title = makeid(10);
                    dataObject = {"content": $("#content").html()};

                    $.ajax({
                        type: "PUT",
                        url: "/api/notes/" + noteId ,
                        data: JSON.stringify(dataObject),
                        contentType: "application/json",
                        dataType: "application/json",
                        statusCode: {
                            200: function () {
                                console.log("Note Updated Successfully")
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
        console.log(annotationId);
        console.log($("[annotation-id =" +  annotationId + "]").css("background-color"));
        jscolor.val(rgbToHex($("[annotation-id =" +  annotationId + "]").css("background-color")).toUpperCase());
        hoverdiv(e,"changeColorForm");
        document.getElementById("jscolor").jscolor.show();
    });

    $("#cancel").click(function(e) {
       $("#changeColorForm").css("display","none");
    });

    $("#changeColorForm > div").click(function(e) {
        document.getElementById('jscolor').jscolor.show();
    });

    $("#categoryNameField").on('input', function() {
        if($(this).val().trim() == ""){
            $("#createCategory").prop('disabled', true);
            $("#createCategoryErrorMessage").text("Category Name cannot be empty !");
            $('#createCategoryErrorMessage').fadeIn('fast', function(){
            });
        }
        if($(this).val().trim() != "" && $(this).val().trim().length < 51){
            $("#createCategory").prop('disabled', false);
        }
        if($(this).val().trim().length > 50){
            $("#createCategory").prop('disabled', true);
            $("#createCategoryErrorMessage").text("Category Name cannot be longer than 50 characters !");
            $('#createCategoryErrorMessage').fadeIn('fast', function(){
            });
        }
    });

    $("#toggleComments").click(function() {
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

    $("#colors span").click(function(e) {
         annotationColor = $(this).css("background-color");
         console.log(annotationColor);
        //annotationColor = 'red';
            editMode = true;
            //annotationColor = rgbToHex(annotationColor).toUpperCase();
            //  annotationColor = rgbToHex(annotationColor).toUpperCase();
            console.log(annotationColor);
            $("#colors").css("display","none");
            switch (annotationColor) {
                case "CCFFCC":
                    $("body").css('cursor', 'url(../cursors/highlight_CCFFCC.cur), auto');
                    break;
                case "FF99CC":
                    $("body").css('cursor', 'url(../cursors/highlight_FF99CC.cur), auto');
                    break;
                case "FFFF99":
                    $("body").css('cursor', 'url(../cursors/highlight_FFFF99.cur), auto');
                    break;
                case "FFCC99":
                    $("body").css('cursor', 'url(../cursors/highlight_FFCC99.cur), auto');
                    break;
                default:
                    $("body").css('cursor', 'url(../cursors/highlight_FFCC99.cur), auto');
            }
    });

    $("#highlighter").click(function(e) {
        if ($(this).attr("class").indexOf("disable") > -1) {
            console.log("no paimt");
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
    });

    $("#cancelComment").click(function(e) {
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

    $("#saveComment").click(function(e) {
        var dataObject = {
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
                    var o = $.parseJSON(e.responseText);
                    var date = convertUTCDateToLocalDate(new Date(o.createdTs));
                    $("#commentBlock").css("display","none");
                    $('textarea').val("");
                    $('#commentsMessage').text("");
                    var li = $("<li class='commentList'></li>");
                    var email = $("<div class='email'><div>").text(o.userName);
                    var date = $("<div class='date'><div>").text(date.toLocaleString());
                    var comment = $("<div class='comment'><div>").text(o.comment);
                    var div = $("<div class='rectangle' annotation-id=" + "'" + o.annotationId + "'" + "></div>");
                    var trash = $("<span class='deleteComment'><i class='fa fa-trash fa-2x' aria-hidden='true'></i></span>");
                    trash.attr("id",o.id);
                    div.append(trash);
                    div.append(email);
                    div.append(date);
                    div.append(comment);
                    //div.css("background-color",$("[annotation-id =" +  annotationId + "]").css("background-color"));
                    div.css("background-color",$("[annotation-id =" +  annotationId + "]").css("background-color"));
                    li.append(div);
                    //$("#commentContainer").prepend(li);
                    $("#commentContainer li:nth-child(2)").after(li);
                    //commentContainer.css("display","block");
                }
            }
        });
    });

        $(function () {
            $("#content").bind('mouseup', function (e) {
                if(editMode){
                    color(annotationColor);
                }
            });
        });

        function color(color) {
            var select = getSelected();
            if (/\S/.test(select)) {

                sel = window.getSelection();
                if (sel.rangeCount && sel.getRangeAt) {
                    range = sel.getRangeAt(0);
                }
                document.designMode = "on";
                if (range) {
                    sel.removeAllRanges();
                    sel.addRange(range);
                }
                document.execCommand("BackColor", false, color);
                document.designMode = "off";

                setTimeout(function () {
                    if (range.commonAncestorContainer.nodeType == '3') {
                        annotationId = uuidv4();
                        console.log("nodeType 3 annotationId: " + annotationId);
                        console.log("nodeType " + range.commonAncestorContainer.nodeType)
                        var span = sel.anchorNode.parentNode;
                        span.setAttribute("annotation-id", annotationId)
                    } else if (range.commonAncestorContainer.nodeType == '1') {
                        console.log("nodeType " + range.commonAncestorContainer.nodeType)
                        annotationId = uuidv4();
                        var test = range.commonAncestorContainer.querySelectorAll('span').forEach(function (e) {
                            if (sel.containsNode(e, true)) {
                                if (e.style.backgroundColor == color) {
                                    console.log(e.style.backgroundColor);
                                    console.log(e);
                                    console.log("nodeType 1 annotationId: " + annotationId);
                                    e.setAttribute("annotation-id", annotationId);
                                }
                            } else {

                            }
                        });
                    }

                    var dataObject = {
                        "annotationId": annotationId,
                        "noteId": noteId,
                       // "annotationCategoryId":$('.selected').attr("id"),
                        "annotationCategoryId":annotationCategoryId,
                        "content": $("[annotation-id=" + annotationId + "]").text(),
                        "color":'#' + rgbToHex(color).toUpperCase()
                    };
                    $.ajax({
                        type: "POST",
                        url: "/api/annotation",
                        data: JSON.stringify(dataObject),
                        contentType: "application/json",
                        dataType: "application/json",
                        statusCode: {
                            201: function (e) {
                                console.log("Annotation Created Successfully");
                                dataObject = {"content": $("#content").html()};

                                $.ajax({
                                    type: "PUT",
                                    url: "/api/notes/" + noteId ,
                                    data: JSON.stringify(dataObject),
                                    contentType: "application/json",
                                    dataType: "application/json",
                                    statusCode: {
                                        200: function () {
                                            console.log("Note Updated Successfully")
                                        }
                                    }
                                });
                                listAnnotationsByCategory();
                            }
                        }
                    });
                }, 200);
            } else {
            }
        }

            $("body").click(function(e) {
                if (e.target.getAttribute('class') == "fa fa-trash fa-2x") {
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
                            }
                        }
                    }
                });
              }
            });

    $("#content").click(function(e) {
            if (e.target.getAttribute('annotation-id')) {
                var attribute = e.target.getAttribute('annotation-id');
                console.log(attribute)
                $.ajax({
                    type: "GET",
                    url: "/api/comment/" + attribute,
                    success: function (result) {
                        if (result.length > 0) {
                            $('#commentContainer li').slice(2).remove();
                            $('#commentsMessage').text("");
                            $("#message").text("");
                            var date;
                            for (var i = 0; i < result.length; i++) {
                                var o = result[i];
                                date = convertUTCDateToLocalDate(new Date(o.createdTs));
                                var li = $("<li class='commentList'></li>");
                                var email = $("<div class='email'><div>").text(o.userName);
                                var date = $("<div class='date'><div>").text(date.toLocaleString());
                                var comment = $("<div class='comment'><div>").text(o.comment);
                                var div = $("<div class='rectangle' annotation-id=" + "'" + attribute + "'" + "></div>");
                                var trash = $("<span class='deleteComment'><i class='fa fa-trash fa-2x' aria-hidden='true'></i></span>");
                                trash.attr("id",o.id);
                                div.append(trash);
                                div.append(email);
                                div.append(date);
                                div.append(comment);
                                div.css("background-color",$("[annotation-id =" +  annotationId + "]").css("background-color"));
                                li.append(div);
                                commentContainer.append(li);
                            }
                        } else {
                            $('#commentContainer li').slice(2).remove();
                            $("#commentsMessage").text("No comments for this annotation");
                        }
                    }
                });
            } //else {
                //$('li').slice(1).remove();
                //$("#message").text("Please click on an annotation to view the comments for it.");
           // }
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

        $("#save").click(function () {
            var btn = $(this);
            title = makeid(10);
            var dataObject = {"content": $("#content").html()};
            //btn.prop('disabled', true);
            $.ajax({
                type: "PUT",
                url: "/api/notes/" + noteId,
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
                        // btn.prop('disabled', true);
                        //}, 1000);
                        //modal.css("display", "block");
                        // value1 = $("#name").val();
                        //value2 = $("#lastName").val();
                        //$('.trigger-btn').click();

                    },
                    400: function (data) {
                        /*  var json = $.parseJSON(data.responseText);
                          setTimeout(function () {
                              btn.prop('disabled', false);
                          }, 1000);
                          $("#userError>strong").text(json.message);
                          $('#userError').fadeIn('fast', function () {
                              //$('.isa_error').delay(3000).fadeOut();
                          });*/
                    }
                }
            });

        });


// Get the modal
        var modal = document.getElementById('myModal');

// Get the button that opens the modal
        var btn = document.getElementById("myBtn");

// Get the <span> element that closes the modal
        var span = document.getElementsByClassName("close")[0];

// When the user clicks the button, open the modal 
//btn.onclick = function() {
        //modal.style.display = "block";
//}

// When the user clicks on <span> (x), close the modal
        span.onclick = function () {
            modal.style.display = "none";
        }

// When the user clicks anywhere outside of the modal, close it
        window.onclick = function (event) {
            if (event.target == modal) {
                modal.style.display = "none";
            }
        }
});