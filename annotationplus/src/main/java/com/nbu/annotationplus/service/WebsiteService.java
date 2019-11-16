package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoHtml;
import com.nbu.annotationplus.dto.DtoWebsite;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebsiteService {

    @Transactional
    public ResponseEntity<DtoHtml> loadUrl(DtoWebsite dtoWebsite) {
        DtoHtml dtoHtml = new DtoHtml();
        try {
            Document document = Jsoup.connect(dtoWebsite.getUrl()).get();
            String html = document.body().html();
            //dtoHtml.setHtml(document.toString());
            dtoHtml.setHtml(html);
        } catch (Exception e){
            throw new InvalidInputParamsException("Unable to open provided url");
        }
        return new ResponseEntity<DtoHtml>(dtoHtml, HttpStatus.OK);
    }
}
