$(document).ready(function() {
    var BreakException = {};

    var editSourceButton = $('#editSource');
    var deleteSourceButton = $('#deleteSourceModal');
    var openSourceButton = $('#openSource');
    var newSourceButton = $('#newSource');
    var editCategoryButton = $("#editCategory");
    var deleteCategoryButton = $("#deleteCategoryModal");
    var delCategoryButton = $("#deleteCategory");
    var delSourceButton = $("#deleteSource");
    var uploadFileButton = $("#uploadFileButton");
    var inputFileField = $("#exampleFormControlFile1");
    var formContainer = $(".formContainer");
    var file = '';
    var extension= '';
    var allowedExtensions = ["pdf", "docx", "doc", "txt"];
    var noteId = '';
    var categoryId = '';
    var categoryName = '';
    var noteName = '';
    var responseJson = '';


    function animateSuccess(message){
        $('.notify')
            .removeClass("do-show");
        setTimeout(
            function()
            {
                $('.notify').addClass("do-show").text(message)
            }, 1);
    }


    function selectRowByValue(value) {
        var httpRequest = new XMLHttpRequest();
        httpRequest.open('GET', '/api/category');
        httpRequest.send();
        httpRequest.onreadystatechange = function () {
            if (httpRequest.readyState === 4 && httpRequest.status === 200) {
                var httpResult = JSON.parse(httpRequest.responseText);
                categoryGridOptions.api.setRowData(httpResult);
                try{
                    categoryGridOptions.api.forEachNode(function (selectedNode) {
                        if (selectedNode.data.name === value) {
                            selectedNode.setSelected(true);
                            throw BreakException;
                        }
                    });
                } catch (e) {
                    if (e !== BreakException){throw e;}
                }
            }
        };
    }

    function handleEmptyCategoryGrid(){
        categoryGridOptions.api.sizeColumnsToFit();
        var selectedCategoryRows = categoryGridOptions.api.getSelectedRows();
        var length = selectedCategoryRows.length;
        if(length < 1){
            newSourceButton.prop('disabled', true);
            $('#editCategory').prop('disabled', true);
            $('#deleteCategoryModal').prop('disabled', true);
            editSourceButton.prop('disabled', true);
            openSourceButton.prop('disabled', true);
            deleteSourceButton.prop('disabled', true);
            sourceGridOptions.api.setRowData([]);
        }
    }

    function convertUTCDateToLocalDate(date) {
        var newDate = new Date(date.getTime() + date.getTimezoneOffset() * 60 * 1000);
        var offset = date.getTimezoneOffset() / 60;
        var hours = date.getHours();
        newDate.setHours(hours - offset);
        return newDate;
    }

    var categoryColumnDefs = [
        {headerName: "Category Name", field: "name", checkboxSelection: true, sortable: true, sort: 'asc'},
        {headerName: "Id", field: "id", hide: true}
    ];

    var categoryGridOptions = {
        defaultColDef: {
            resizable: true,
            filter: true
        },
        columnDefs: categoryColumnDefs,
        rowSelection: 'single',
        paginationAutoPageSize:true,
        pagination: true,
        onSelectionChanged: onCategorySelection,
        onFirstDataRendered: onFirstDataRendered,
        onGridReady:handleEmptyCategoryGrid

    };

    function onCategorySelection() {
        var selectedCategoryRows = categoryGridOptions.api.getSelectedRows();
        var length = selectedCategoryRows.length;
        if (length > 0) {
            newSourceButton.prop('disabled', false);
            categoryId = '';
            categoryName = '';
            selectedCategoryRows.forEach(function (selectedRow) {
            categoryId += selectedRow.id;
            categoryName += selectedRow.name;
        });
            $('#editCategory').prop('disabled', false);
            $('#deleteCategoryModal').prop('disabled', false);
            populateSourceGridByCategoryId();
    } else {
            handleEmptyCategoryGrid();
        }
    }

    var categoryGridDiv = document.querySelector('#categoryGrid');

    new agGrid.Grid(categoryGridDiv, categoryGridOptions);

    function populateCategoryGrid(){
        var httpRequest = new XMLHttpRequest();
        httpRequest.open('GET', '/api/category');
        httpRequest.send();
        httpRequest.onreadystatechange = function () {
            if (httpRequest.readyState === 4 && httpRequest.status === 200) {
                var httpResult = JSON.parse(httpRequest.responseText);
                if(httpResult.length < 1){
                    $("#noCategoryInfo").css("display","block");
                } else {
                    $("#noCategoryInfo").css("display","none");
                }
                categoryGridOptions.api.setRowData(httpResult);
                //categoryGridOptions.api.selectIndex(0, false, false);
                categoryGridOptions.api.forEachNode(function (selectedNode) {
                    selectedNode.rowIndex ? 0 : selectedNode.setSelected(true);
                });
                handleEmptyCategoryGrid();
            }
        };
    }
    populateCategoryGrid();

    function dateParser(params) {
        if (params.value != null) {
            var date = convertUTCDateToLocalDate(new Date(params.value));
            return date.toLocaleString();
        } else {
            return '';
        }
    }

    var sourceColumnDefs = [
        {headerName: "Source Name", field: "title", checkboxSelection: true, sortable: true,},
        {headerName: "Date Created", field: "createdTs", sortable: true, cellRenderer: dateParser},
        {headerName: "Last Updated", field: "updatedTs", sortable: true, sort: 'desc', cellRenderer: dateParser},
        {headerName: "Id", field: "id", hide: true}
    ];

    var sourceGridOptions = {
        defaultColDef: {
            resizable: true,
            filter:true
        },
        columnDefs: sourceColumnDefs,
        rowSelection: 'single',
        paginationAutoPageSize:true,
        pagination: true,
        onSelectionChanged: onSelectionChanged,
        onFirstDataRendered: onFirstDataRendered,
        onGridReady:onFirstDataRendered
    };

    function onFirstDataRendered(params) {
        params.api.sizeColumnsToFit();
    }

    function onSelectionChanged() {
        var selectedRows = sourceGridOptions.api.getSelectedRows();
        var length = selectedRows.length;
        if(length > 0) {
            editSourceButton.prop('disabled', false);
            openSourceButton.prop('disabled', false);
            deleteSourceButton.prop('disabled', false);
            noteId = '';
            noteName = '';
            selectedRows.forEach(function (selectedRow) {
                noteId += selectedRow.id;
                noteName += selectedRow.title;
            });
        } else {
            editSourceButton.prop('disabled', true);
            openSourceButton.prop('disabled', true);
            deleteSourceButton.prop('disabled', true);
        }
    }

    var sourceGridDiv = document.querySelector('#sourceGrid');

    new agGrid.Grid(sourceGridDiv, sourceGridOptions);

function populateSourceGridByCategoryId(){
    var httpRequest = new XMLHttpRequest();
    httpRequest.open('GET', '/api/notes/category=' + categoryId);
    httpRequest.send();
    httpRequest.onreadystatechange = function() {
        if (httpRequest.readyState === 4 && httpRequest.status === 200) {
            var httpResult = JSON.parse(httpRequest.responseText);
            sourceGridOptions.api.setRowData(httpResult);
        }
    };
}

    newSourceButton.click(function() {
        showSourceFile();
        $('#sourceType').prop('selectedIndex',0);
        $("#fileUploadContainer").css("display","block");

    });

    $("#newCategory").click(function() {
        $("#createCategoryContainer").css("display","block");
        $("#createCategory").prop("disabled",true)
    });

    deleteCategoryButton.click(function() {
        $("#deleteCategoryConfirmationContainer").css("display","block");
    });

    deleteSourceButton.click(function() {
        $("#deleteSourceConfirmationContainer").css("display","block");
    });

    $(".closeDialog").click(function() {
        formContainer.css("display","none");
    });

    editSourceButton.click(function() {
        $("#updateSourceContainer").css("display","block");
        $("#sourceName").val(noteName);
        $("#updateSourceName").prop('disabled', true);
    });

    editCategoryButton.click(function() {
        $("#updateCategoryContainer").css("display","block");
        $("#categoryName").val(categoryName);
        $("#updateCategoryName").prop('disabled', true);
    });

    delCategoryButton.click(function() {
        $(this).prop("disabled",true);
        $.ajax({
            type: "DELETE",
            url: "/api/category/" + categoryId,
            success: function () {
                formContainer.css("display","none");
                animateSuccess("Category: " + "'" + categoryName + "'" + " deleted successfully");
                populateCategoryGrid();
                delCategoryButton.prop("disabled",false);
            }
        });
    });

    delSourceButton.click(function() {
        $(this).prop("disabled",true);
        $.ajax({
            type: "DELETE",
            url: "/api/notes/" + noteId,
            success: function () {
                formContainer.css("display","none");
                animateSuccess("Source: " + "'" + noteName + "'" + " deleted successfully");
                populateSourceGridByCategoryId();
                editSourceButton.prop('disabled', true);
                openSourceButton.prop('disabled', true);
                deleteSourceButton.prop('disabled', true);
                delSourceButton.prop("disabled",false);
            }
        });
    });

    openSourceButton.click(function() {
        $(this).prop('disabled', true);
        window.open("/admin/note?id=" + noteId, "_self")
    });

    $("#createCategory").click(function() {
        $(this).prop("disabled",true);
        $(".categoryErrorMessage").text("");
        var dataObject = {
            "name":$("#categoryNameCreateValue").val().trim()
        };
        $.ajax({
            type: "POST",
            url: "/api/category",
            data: JSON.stringify(dataObject),
            contentType: "application/json",
            dataType: "application/json",
            statusCode: {
                201: function (e) {
                    $("#noCategoryInfo").css("display","none");
                    responseJson = $.parseJSON(e.responseText);
                    formContainer.css("display","none");
                    animateSuccess("Category: " + "'" + responseJson.name + "'" + " created successfully");
                    $("#createCategory").prop("disabled",false);
                    $("#categoryNameCreateValue").val("");
                    selectRowByValue(responseJson.name);
                },
                400: function (e) {
                    responseJson = $.parseJSON(e.responseText);
                    $(".categoryErrorMessage").text(responseJson.message);
                    $("#createCategory").prop("disabled",false);
                }
            }
        });
    });

    $("#updateCategoryName").click(function() {
        $(this).prop("disabled",true);
        $(".categoryErrorMessage").text("");
        var dataObject = {
            "name":$("#categoryName").val().trim()
        };
        $.ajax({
            type: "PUT",
            url: "/api/category/" + categoryId,
            data: JSON.stringify(dataObject),
            contentType: "application/json",
            dataType: "application/json",
            statusCode: {
                200: function (e) {
                    responseJson = $.parseJSON(e.responseText);
                    formContainer.css("display","none");
                    animateSuccess("Category: " + "'" + responseJson.name + "'" + " updated successfully");
                    $("#updateCategoryName").prop("disabled",false);
                    selectRowByValue(responseJson.name);
                },
                400: function (e) {
                    responseJson = $.parseJSON(e.responseText);
                    $(".categoryErrorMessage").text(responseJson.message);
                    $("#updateCategoryName").prop("disabled",false);
                }
            }
        });
    });

    $("#updateSourceName").click(function() {
        $(this).prop("disabled",true);
        var dataObject = {
            "title":$("#sourceName").val()
        };
        $.ajax({
            type: "PUT",
            url: "/api/notes/" + noteId,
            data: JSON.stringify(dataObject),
            contentType: "application/json",
            dataType: "application/json",
            statusCode: {
                200: function () {
                    formContainer.css("display","none");
                    $("#updateSourceName").prop("disabled",false);
                    animateSuccess("Source: " + "'" + noteName + "'" + " updated successfully");
                    populateSourceGridByCategoryId();
                }
            }
        });
    });

    $(".close").click(function() {
        formContainer.css("display","none");
        $("#categoryNameCreateValue").val("");
        inputFileField.val("");
        uploadFileButton.prop("disabled", true);
        $(".errorMessages").text("");
    });


    inputFileField.on("input" ,function () {
        file = $(this)[0].files[0];
        extension = file.name.split('.').pop().toLowerCase();
        var isFileValid = (allowedExtensions.indexOf(extension) > -1);
        if($(this)[0].files.length < 1){
            uploadFileButton.prop("disabled", true);
        } else {
            if(isFileValid){
                uploadFileButton.prop("disabled", false);
            } else {
                uploadFileButton.prop("disabled", true);
            }
        }
    });

    uploadFileButton.click(function() {
        file  = (inputFileField)[0].files[0];
        var fileName = file.name;

        extension = file.name.split('.').pop().toLowerCase();
        console.log(extension);
        var isFileValid = (allowedExtensions.indexOf(extension) > -1);

        if((isFileValid)){
            setTimeout(function () {
                var formData = new FormData();
                formData.append('file', file);
                $.ajax({
                    type: "POST",
                    url: "/api/readFile",
                    data: formData,
                    contentType: false,
                    processData: false,
                    mimeType: "multipart/form-data",
                    statusCode: {
                        200: function (e) {
                           // console.log(e);
                            var json2 = $.parseJSON(e);
                            var html = json2.html;
                            var dataObject = {"title": fileName, "content": html, "categoryId": categoryId};
                            $.ajax({
                                type: "POST",
                                url: "/api/notes/",
                                data: JSON.stringify(dataObject),
                                contentType: "application/json",
                                dataType: "application/json",
                                statusCode: {
                                    201: function (e) {
                                        var json = $.parseJSON(e.responseText);
                                       // console.log(json.id);
                                        window.open("/admin/note?id=" + json.id, "_self")
                                    },
                                    400: function (data) {
                                    }
                                }
                            });
                        },
                        400: function () {
                            console.log("error");
                        }
                    }
                });
            }, 100);
        } else {
            console.log("Invalid File Format")
        }
    });

    $('#categoryNameCreateValue').on('input', function() {
        if($(this).val().trim() == "" ){
            $(".categoryErrorMessage").text("Name cannot be empty");
            $("#createCategory").prop('disabled', true);
        }else if(/[^a-zA-Z ]/.test($(this).val())){
            $(".categoryErrorMessage").text("Name cannot contain numbers and special characters");
            $("#createCategory").prop('disabled', true);
        } else if($(this).val().trim().length > 50){
            $(".categoryErrorMessage").text("Name cannot be longer than 50 characters");
            $("#createCategory").prop('disabled', true);
        } else if($(this).val().trim() != "" && !/[^a-zA-Z ]/.test($(this).val()) && $(this).val().trim().length < 51) {
            $(".categoryErrorMessage").text("");
            $("#createCategory").prop('disabled', false);
        }
    });

    $('#categoryName').on('input', function() {
        if($(this).val().trim() == "" ){
            $(".categoryErrorMessage").text("Name cannot be empty");
            $("#updateCategoryName").prop('disabled', true);
        } else if($(this).val().trim() === categoryName){
            $(".categoryErrorMessage").text("");
            $("#updateCategoryName").prop('disabled', true);
        } else if(/[^a-zA-Z ]/.test($(this).val())){
            $(".categoryErrorMessage").text("Name cannot contain numbers and special characters");
            $("#updateCategoryName").prop('disabled', true);
        } else if($(this).val().trim().length > 50){
            $(".categoryErrorMessage").text("Name cannot be longer than 50 characters");
            $("#updateCategoryName").prop('disabled', true);
        } else if($(this).val().trim() != "" && !/[^a-zA-Z ]/.test($(this).val()) && $(this).val().trim().length < 51 && $(this).val() !== categoryName) {
            $(".categoryErrorMessage").text("");
            $("#updateCategoryName").prop('disabled', false);
        }
    });

    $('#sourceName').on('input', function() {
        if($(this).val().trim() == "" ){
            $("#sourceErrorMessage").text("Name cannot be empty");
            $("#updateSourceName").prop('disabled', true);
        } else if($(this).val().trim() === noteName) {
            $(".categoryErrorMessage").text("");
            $("#updateSourceName").prop('disabled', true);
        } else if($(this).val().trim().length > 50){
            $("#sourceErrorMessage").text("Name cannot be longer than 50 characters");
            $("#updateSourceName").prop('disabled', true);
        } else if($(this).val().trim() != "" && $(this).val().trim().length < 51 && $(this).val() !== noteName) {
            $("#sourceErrorMessage").text("");
            $("#updateSourceName").prop('disabled', false);
        }
    });
















  /*  $('.button').on('click', function(event){
        var type = "top-left"
        var status = "success"

        $('.button').removeClass('is-active');
        $(this).addClass('is-active');

        $('.notify')
            .removeClass()
            .attr('data-notification-status', status)
            .addClass(type + ' notify')
            .addClass('do-show');

        event.preventDefault();
    })*/

  function showSourceUrl(){
      $("#sourceFileContainer").css("display","none");
      $("#sourceUrlContainer").css("display","block");
  }

  function showSourceFile(){
        $("#sourceUrlContainer").css("display","none");
        $("#sourceFileContainer").css("display","block");
  }


    $( "#sourceType" ).change(function() {
        if($(this).val() === "document"){
            showSourceFile();
        }if($(this).val() === "website"){
            showSourceUrl();
        }
    });
});























   /* function makeid(length) {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        for (var i = 0; i < length; i++)
            text += possible.charAt(Math.floor(Math.random() * possible.length));

        return text;
    }*/
/*
    var holder = document.getElementById('holder'),
        state = document.getElementById('status');

    if (typeof window.FileReader === 'undefined') {
        state.className = 'fail';
    } else {
        state.className = 'success';
        state.innerHTML = 'File API & FileReader available';
    }

    holder.ondragover = function() {
        this.className = 'hover';
        return false;
    };
    holder.ondragend = function() {
        this.className = '';
        return false;
    };
    holder.ondrop = function(e) {
        this.className = '';
        e.preventDefault();

        var file = e.dataTransfer.files[0];
        var extension = file.name.split('.').pop().toLowerCase();
        console.log(extension);
        var allowedExtensions = ["pdf", "docx", "doc", "txt"];
        var isFileValid = (allowedExtensions.indexOf(extension) > -1);

    if((isFileValid)){
        setTimeout(function () {
            var formData = new FormData();
            formData.append('file', file);
            $.ajax({
                type: "POST",
                url: "/api/readFile",
                data: formData,
                contentType: false,
                processData: false,
                mimeType: "multipart/form-data",
                statusCode: {
                    200: function (e) {
                            console.log(e);
                            var json2 = $.parseJSON(e);
                            var html = json2.html;
                            var title = makeid(10);
                            var dataObject = {"title": title, "content": html, "categoryId": 7};
                            $.ajax({
                                type: "POST",
                                url: "/api/notes/",
                                data: JSON.stringify(dataObject),
                                contentType: "application/json",
                                dataType: "application/json",
                                statusCode: {
                                    201: function (e) {
                                        var json = $.parseJSON(e.responseText);
                                        console.log(json.id);
                                        window.open("/admin/note?id=" + json.id, "_self")
                                    },
                                    400: function (data) {
                                    }
                                }
                            });
                    },
                    400: function () {
                        console.log("error");
                    }
                }
            });
        }, 100);
    } else {
        console.log("Invalid File Format")
    }*/

  /*  else {
            var x = URL.createObjectURL(file);
            function loadFile(url,callback){
                JSZipUtils.getBinaryContent(url,callback);
            }
            function generate() {
                loadFile(x,function(error,content){
                    if (error) { throw error };
                    var zip = new JSZip(content);
                    var doc=new window.docxtemplater().loadZip(zip)
                    var text=doc.getFullText();
                    //console.log(text);
                    var dataObject = {"title": "fffffgfgfgfff", "content": text, "categoryId": 1};
                    $.ajax({
                        type: "POST",
                        url: "/api/notes/",
                        data: JSON.stringify(dataObject),
                        contentType: "application/json",
                        dataType: "application/json",
                        statusCode: {
                            201: function (e) {
                                var json = $.parseJSON(e.responseText);
                                console.log(json.id);
                                window.open("/admin/note?id=" + json.id, "_self")
                            },
                            400: function (data) {
                            }
                        }
                    });
                })
            }
            generate();
        }
    }
});
*/



/*function readURL(input) {
    if (input.files && input.files[0]) {
        var extension = input.files[0].name.split('.').pop().toLowerCase(),  //file extension from input file
            isSuccess = fileTypes.indexOf(extension) > -1;  //is extension in acceptable types

        if (isSuccess) { //yes
            var reader = new FileReader();
            reader.onload = function (e) {
                alert('image has read completely!');
            }

            reader.readAsDataURL(input.files[0]);
        }
        else { //no
            //warning
        }
    }
}*/