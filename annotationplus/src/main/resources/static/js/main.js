$(document).ready(function() {
    var BreakException = {};
    var editSourceButton = $('#editSource');
    var viewAnnotationButton = $('#viewAnnotation');
    var deleteSourceButton = $('#deleteSourceModal');
    var openSourceButton = $('#openSource');
    var newSourceButton = $('#newSource');
    var editCategoryButton = $("#editCategory");
    var deleteCategoryButton = $("#deleteCategoryModal");
    var delCategoryButton = $("#deleteCategory");
    var delSourceButton = $("#deleteSource");
    var uploadFileButton = $("#uploadFileButton");
    var loadUrlButton = $("#loadUrlButton");
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
    var annotationId;

    function validateUrl(str) {
        var regex = /(http|https):\/\/(\w+:{0,1}\w*)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%!\-\/]))?/;
        if(!regex .test(str)) {
            return false;
        } else {
            return true;
        }
    }

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
    httpRequest.open('GET', '/api/note?categoryId=' + categoryId);
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
                populateAnnotationGrid();
                delCategoryButton.prop("disabled",false);
            }
        });
    });

    delSourceButton.click(function() {
        $(this).prop("disabled",true);
        $.ajax({
            type: "DELETE",
            url: "/api/note/" + noteId,
            success: function () {
                formContainer.css("display","none");
                animateSuccess("Source: " + "'" + noteName + "'" + " deleted successfully");
                populateSourceGridByCategoryId();
                populateAnnotationGrid();
                editSourceButton.prop('disabled', true);
                openSourceButton.prop('disabled', true);
                deleteSourceButton.prop('disabled', true);
                delSourceButton.prop("disabled",false);
            }
        });
    });

    openSourceButton.click(function() {
        $(this).prop('disabled', true);
        window.open("/admin/source?id=" + noteId, "_self")
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
                    if (e.redirect) {
                        // data.redirect contains the string URL to redirect to
                        window.location.href = e.redirect;
                    }
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
                    if (e.redirect) {
                        // data.redirect contains the string URL to redirect to
                        window.location.href = e.redirect;
                    }
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
                }/*,
                302: function () {
                    console.log("302");
                    window.open("/login","_self");
                    //responseJson = $.parseJSON(e.responseText);
                    //$(".categoryErrorMessage").text(responseJson.message);
                    //$("#updateCategoryName").prop("disabled",false);
                }*/
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
            url: "/api/note/" + noteId,
            data: JSON.stringify(dataObject),
            contentType: "application/json",
            dataType: "application/json",
            statusCode: {
                200: function () {
                    formContainer.css("display","none");
                    $("#updateSourceName").prop("disabled",false);
                    animateSuccess("Source: " + "'" + noteName + "'" + " updated successfully");
                    populateSourceGridByCategoryId();
                },
                400:function (e) {
                    responseJson = $.parseJSON(e.responseText);
                    $("#sourceErrorMessage").text(responseJson.message);
                    $("#updateSourceName").prop("disabled",false);
                }
            }
        });
    });

    $(".close").click(function() {
        formContainer.css("display","none");
        $("#categoryNameCreateValue").val("");
        inputFileField.val("");
        $("#sourceURL").val("");
        uploadFileButton.prop("disabled", true);
        loadUrlButton.prop("disabled", true);
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
        uploadFileButton.prop('disabled', true);
        file  = (inputFileField)[0].files[0];
        var fileName = file.name;

        extension = file.name.split('.').pop().toLowerCase();
        var isFileValid = (allowedExtensions.indexOf(extension) > -1);

        if((isFileValid)){
            //setTimeout(function () {
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
                            responseJson = $.parseJSON(e);
                            var html = responseJson.html;
                            var dataObject = {"title": fileName, "content": html, "category": {"id":categoryId}};
                            $.ajax({
                                type: "POST",
                                url: "/api/note/",
                                data: JSON.stringify(dataObject),
                                contentType: "application/json",
                                dataType: "application/json",
                                statusCode: {
                                    201: function (e) {
                                        responseJson = $.parseJSON(e.responseText);
                                        window.open("/admin/source?id=" + responseJson.id, "_self");
                                    },
                                    400: function (e) {
                                        responseJson = $.parseJSON(e.responseText);
                                        $("#uploadFileErrorMessage").text(responseJson.message);
                                        uploadFileButton.prop('disabled', false);
                                    }
                                }
                            });
                        },
                        400: function (e) {
                            responseJson = $.parseJSON(e.responseText);
                            $("#uploadFileErrorMessage").text(responseJson.message);
                            uploadFileButton.prop('disabled', false);
                        }
                    }
                });
           // }, 100);
        } else {
            $("#uploadFileErrorMessage").text("Invalid File Format");
            uploadFileButton.prop('disabled', false);
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
        }  else if($(this).val().trim() != "" && $(this).val() !== noteName) {
            $("#sourceErrorMessage").text("");
            $("#updateSourceName").prop('disabled', false);
        }
    });

    $('#sourceURL').on('input', function() {
        if($(this).val().trim() == "" ){
            $("#sourceUrlErrorMessage").text("Source URL cannot be empty");
            loadUrlButton.prop('disabled', true);
        }else if(!validateUrl($(this).val())) {
            $("#sourceUrlErrorMessage").text("Invalid URL");
            loadUrlButton.prop('disabled', true);
        }else if(validateUrl($(this).val())) {
            $("#sourceUrlErrorMessage").text("");
            loadUrlButton.prop('disabled', false);
        }
    });

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

    loadUrlButton.click(function() {
        loadUrlButton.prop('disabled', true);
        var sourceUrl = $('#sourceURL').val();
        var dataObject = {
            "url": sourceUrl
        };
        $.ajax({
            type: "POST",
            url: "/api/loadUrl",
            data: JSON.stringify(dataObject),
            contentType: "application/json",
            dataType: "application/json",
                    statusCode: {
                        200: function (e) {
                            responseJson = $.parseJSON(e.responseText);
                            var html = responseJson.html;
                            dataObject = {"title": sourceUrl, "content": html, "category": {"id":categoryId}};
                            $.ajax({
                                type: "POST",
                                url: "/api/note/",
                                data: JSON.stringify(dataObject),
                                contentType: "application/json",
                                dataType: "application/json",
                                statusCode: {
                                    201: function (e) {
                                        responseJson = $.parseJSON(e.responseText);
                                        window.open("/admin/source?id=" + responseJson.id, "_self");
                                        $('#sourceURL').val("");
                                    },
                                    400: function (e) {
                                        responseJson = $.parseJSON(e.responseText);
                                        $("#sourceUrlErrorMessage").text(responseJson.message);
                                        loadUrlButton.prop('disabled', false);
                                    }
                                }
                            });
                        },
                        400: function (e) {
                            responseJson = $.parseJSON(e.responseText);
                            $("#sourceUrlErrorMessage").text(responseJson.message);
                            loadUrlButton.prop('disabled', false);
                        }
                    }
                });
    });

    viewAnnotationButton.click(function() {
        $(this).prop('disabled', true);
        window.open("/admin/source?id=" + noteId + '&annotation-id=' + annotationId, "_self")
    });

    function onAnnotationSelection() {
        var selectedAnnotationRows = annotationGridOptions.api.getSelectedRows();
        var length = selectedAnnotationRows.length;
        if (length > 0) {
            viewAnnotationButton.prop('disabled', false);
            annotationId = '';
            noteId = '';
            selectedAnnotationRows.forEach(function (selectedRow) {
                annotationId += selectedRow.id;
                noteId = selectedRow.noteId;
            });
        } else {
            viewAnnotationButton.prop('disabled', true);
        }
    }

    var annotationColumnDefs = [
        {headerName: "Content", field: "content", checkboxSelection: true, sortable: true,},
        {headerName: "Date Created", field: "createdTs", sortable: true, sort: 'desc', cellRenderer: dateParser},
        {headerName: "Id", field: "id", hide: true},
        {headerName: "Note Id", field: "noteId", hide: true}
    ];

    var annotationGridOptions = {
        defaultColDef: {
            resizable: true,
            filter: true
        },
        columnDefs: annotationColumnDefs,
        rowSelection: 'single',
        paginationAutoPageSize:true,
        pagination: true,
        onFirstDataRendered: onFirstDataRendered,
        onSelectionChanged: onAnnotationSelection,
        onGridReady:onFirstDataRendered

    };

    var annotationGridDiv = document.querySelector('#annotationGrid');

    new agGrid.Grid(annotationGridDiv, annotationGridOptions);

    function populateAnnotationGrid(){
        var httpRequest = new XMLHttpRequest();
        httpRequest.open('GET', '/api/annotation');
        httpRequest.send();
        httpRequest.onreadystatechange = function () {
            if (httpRequest.readyState === 4 && httpRequest.status === 200) {
                var httpResult = JSON.parse(httpRequest.responseText);
                annotationGridOptions.api.setRowData(httpResult);
            }
        };
    }
    populateAnnotationGrid();
});