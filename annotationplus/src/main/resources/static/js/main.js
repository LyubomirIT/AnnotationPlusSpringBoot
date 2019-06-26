$(document).ready(function() {
    function convertUTCDateToLocalDate(date) {
        var newDate = new Date(date.getTime() + date.getTimezoneOffset() * 60 * 1000);

        var offset = date.getTimezoneOffset() / 60;
        var hours = date.getHours();

        newDate.setHours(hours - offset);

        return newDate;
    }

    var noteId = '';
    var categoryId = '';

    // specify the columns
    var columnDefs = [
        {headerName: "Category Name", field: "name", checkboxSelection: true, sortable: true, sort: 'asc', width:1000 },
        {headerName: "Id", field: "id", hide:true }
    ];

    // let the grid know which columns to use
    var gridOptions = {
        columnDefs: columnDefs,
        rowSelection: 'single',
        onSelectionChanged: onCategorySelection
    };

    function onCategorySelection() {
        var selectedCategoryRows = gridOptions.api.getSelectedRows();
        categoryId = '';
        selectedCategoryRows.forEach( function(selectedRow) {
            categoryId += selectedRow.id;
        });
        console.log(categoryId);
        var httpRequest = new XMLHttpRequest();
        httpRequest.open('GET', '/api/notes/category=' + categoryId);
        httpRequest.send();
        httpRequest.onreadystatechange = function() {
            if (httpRequest.readyState === 4 && httpRequest.status === 200) {
                var httpResult = JSON.parse(httpRequest.responseText);
                gridOptions2.api.setRowData(httpResult);
            }
        };
    }

    // lookup the container we want the Grid to use
    var eGridDiv = document.querySelector('#myGrid');

    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(eGridDiv, gridOptions);

    fetch('/api/category').then(function(response) {
        return response.json();
    }).then(function(data) {
        gridOptions.api.setRowData(data);
    })

    function dateParser(params) {
        if(params.value != null) {
            var date = convertUTCDateToLocalDate(new Date(params.value));
            return date.toLocaleString();
        }else {
            return '';
        }
    }

    // specify the columns
    var columnDefs2 = [
        {headerName: "Source Name", field: "title", checkboxSelection: true, sortable: true,},
        {headerName: "Date Created", field: "createdTs", sortable: true, cellRenderer:dateParser},
        {headerName: "Last Updated", field: "updatedTs", sortable: true, sort: 'desc', cellRenderer:dateParser},
        {headerName: "Id", field: "id", hide:true}
    ];

    // let the grid know which columns to use
    var gridOptions2 = {
        defaultColDef: {
            resizable: true
        },
        columnDefs: columnDefs2,
        rowSelection: 'single',
        onSelectionChanged: onSelectionChanged,
        onFirstDataRendered(params) {
            params.api.sizeColumnsToFit();
        }
    };

   /* function sizeToFit() {
        gridOptions.api.sizeColumnsToFit();
    }*/

    function onSelectionChanged() {
        var selectedRows = gridOptions2.api.getSelectedRows();
        noteId = '';
        selectedRows.forEach( function(selectedRow) {
            noteId += selectedRow.id;
        });
        console.log(noteId);
    }

    // lookup the container we want the Grid to use
    var eGridDiv2 = document.querySelector('#myGrid2');

    // create the grid passing in the div to use together with the columns & data we want to use
    new agGrid.Grid(eGridDiv2, gridOptions2);

    /* fetch('/api/notes').then(function(response) {
         return response.json();
     }).then(function(data) {
         gridOptions2.api.setRowData(data);
     })*/

    var httpRequest = new XMLHttpRequest();
    httpRequest.open('GET', '/api/notes');
    httpRequest.send();
    httpRequest.onreadystatechange = function() {
        if (httpRequest.readyState === 4 && httpRequest.status === 200) {
            var httpResult = JSON.parse(httpRequest.responseText);
            gridOptions2.api.setRowData(httpResult);
        }
    };







    $("#newSource").click(function() {
        $("#fileUploadContainer").css("display","block");
    });

    $("#newCategory").click(function() {
        $("#createCategoryContainer").css("display","block");
    });

    $("#deleteCategoryModal").click(function() {
        $("#deleteCategoryConfirmationContainer").css("display","block");
    });

    $("#editSource").click(function() {
        $.ajax({
                type: "GET",
                url: "/api/notes/" + noteId,
                success: function (result) {
                    $("#updateSourceContainer").css("display","block");
                    $("#sourceName").val(result.title);
                }
            });
    });

    $("#editCategory").click(function() {
        $.ajax({
            type: "GET",
            url: "/api/category/" + categoryId,
            success: function (result) {
                $("#updateCategoryContainer").css("display","block");
                $("#categoryName").val(result.name);
            }
        });
    });

    $("#deleteCategory").click(function() {
        $.ajax({
            type: "DELETE",
            url: "/api/category/" + categoryId,
            success: function (result) {
                var httpRequest = new XMLHttpRequest();
                httpRequest.open('GET', '/api/category');
                httpRequest.send();
                httpRequest.onreadystatechange = function() {
                    if (httpRequest.readyState === 4 && httpRequest.status === 200) {
                        var httpResult = JSON.parse(httpRequest.responseText);
                        gridOptions.api.setRowData(httpResult);
                    }
                };
                $("#deleteCategoryConfirmationContainer").css("display","none");
            }
        });
    });

    $("#deleteSource").click(function() {
        $.ajax({
            type: "DELETE",
            url: "/api/notes/" + noteId,
            success: function () {
                var httpRequest = new XMLHttpRequest();
                httpRequest.open('GET', '/api/notes/category=' + categoryId);
                httpRequest.send();
                httpRequest.onreadystatechange = function() {
                    if (httpRequest.readyState === 4 && httpRequest.status === 200) {
                        var httpResult = JSON.parse(httpRequest.responseText);
                        gridOptions2.api.setRowData(httpResult);
                    }
                };
            }
        });
    });

    $("#openSource").click(function() {
        window.open("/admin/note?id=" + noteId, "_self")
    });

    $("#createCategory").click(function() {
        var dataObject = {
            "name":$("#categoryNameCreateValue").val()
        };
        $.ajax({
            type: "POST",
            url: "/api/category",
            data: JSON.stringify(dataObject),
            contentType: "application/json",
            dataType: "application/json",
            statusCode: {
                201: function () {
                    var httpRequest = new XMLHttpRequest();
                    httpRequest.open('GET', '/api/category');
                    httpRequest.send();
                    httpRequest.onreadystatechange = function() {
                        if (httpRequest.readyState === 4 && httpRequest.status === 200) {
                            var httpResult = JSON.parse(httpRequest.responseText);
                            gridOptions.api.setRowData(httpResult);
                        }
                    };
                    $("#createCategoryContainer").css("display","none");
                }
            }
        });
    });


    $("#updateCategoryName").click(function() {
        var dataObject = {
            "name":$("#categoryName").val()
        };
        $.ajax({
            type: "PUT",
            url: "/api/category/" + categoryId,
            data: JSON.stringify(dataObject),
            contentType: "application/json",
            dataType: "application/json",
            statusCode: {
                200: function () {
                    var httpRequest = new XMLHttpRequest();
                    httpRequest.open('GET', '/api/category');
                    httpRequest.send();
                    httpRequest.onreadystatechange = function() {
                        if (httpRequest.readyState === 4 && httpRequest.status === 200) {
                            var httpResult = JSON.parse(httpRequest.responseText);
                            gridOptions.api.setRowData(httpResult);
                        }
                    };
                    $("#updateCategoryContainer").css("display","none");
                }
            }
        });
    });




    $("#updateSourceName").click(function() {
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
                    var httpRequest = new XMLHttpRequest();
                    httpRequest.open('GET', '/api/notes/category=' + categoryId);
                    httpRequest.send();
                    httpRequest.onreadystatechange = function() {
                        if (httpRequest.readyState === 4 && httpRequest.status === 200) {
                            var httpResult = JSON.parse(httpRequest.responseText);
                            gridOptions2.api.setRowData(httpResult);
                        }
                    };
                    $("#updateSourceContainer").css("display","none");
                }
            }
        });
    });




    $(".close").click(function() {
        $(".formContainer").css("display","none");
    });

    $("#uploadFileButton").click(function() {
        var file  = $("#exampleFormControlFile1")[0].files[0];
        var fileName = file.name;

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