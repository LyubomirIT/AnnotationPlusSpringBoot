package com.nbu.annotationplus.controller;

import com.nbu.annotationplus.dto.DtoHtml;
import com.nbu.annotationplus.dto.DtoWebsite;
import com.nbu.annotationplus.service.WebsiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class WebsiteController {

    @Autowired
    private WebsiteService websiteService;

    @PostMapping("/loadUrl")
    public ResponseEntity<DtoHtml> loadUrl(@Valid @RequestBody DtoWebsite dtoWebsite) {
        return websiteService.loadUrl(dtoWebsite);
    }
}
