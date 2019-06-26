package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.dto.DtoHtml;
import com.nbu.annotationplus.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class FileController {

    @Autowired
    private FileService fileService;


    @PostMapping("/readFile")
    public ResponseEntity<DtoHtml> readFile(@Valid @RequestParam("file") MultipartFile file) {
        return fileService.readFile(file);
    }

    /*@PostMapping("/convertToWord")
    public ResponseEntity<DtoHtml> convertToWord(@Valid @RequestParam("file") MultipartFile file) {
        return fileService.convertWord(file);
    }

    @PostMapping("/convertToWord2")
    public ResponseEntity<DtoHtml> convertToWord2(@Valid @RequestParam("file") MultipartFile file) {
        return fileService.convertWord2(file);
    }

    @PostMapping("/convertToTxt")
    public ResponseEntity<DtoHtml> convertToTxt(@Valid @RequestParam("file") MultipartFile file) {
        return fileService.convertToTxt(file);
    }*/
}
