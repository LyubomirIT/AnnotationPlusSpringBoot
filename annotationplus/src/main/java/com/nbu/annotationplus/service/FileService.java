package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoHtml;
import com.nbu.annotationplus.exception.InvalidInputParamsException;

import com.nbu.annotationplus.utils.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.converter.WordToHtmlUtils;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.fit.pdfdom.PDFDomTree;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

@Service("fileService")
public class FileService {

    @Transactional
    public ResponseEntity<DtoHtml> convertFileToHtml(MultipartFile file) {
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        if(ext == null){
            throw new InvalidInputParamsException("Invalid File");
        }
       if(file.getSize() > FileUtils.MAX_FILE_SIZE_IN_BYTES){
           throw new InvalidInputParamsException("File is too big. Max file size is 5 MB");
        }
        DtoHtml dtoHtml = new DtoHtml();
        if (ext.equalsIgnoreCase("pdf")) {
            try {
                InputStream inputStream = new BufferedInputStream(file.getInputStream());
                StringWriter out = new StringWriter();
                PDDocument pdf = PDDocument.load(inputStream);
                Writer output = new PrintWriter(out, true);
                new PDFDomTree().writeText(pdf, output);
                output.close();
                dtoHtml.setHtml(out.toString());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new InvalidInputParamsException("Error While Reading File");
            }
        } else if (ext.equalsIgnoreCase("doc")){
            try {
                HWPFDocumentCore wordDocument = WordToHtmlUtils.loadDoc(new BufferedInputStream(file.getInputStream()));
                WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                        DocumentBuilderFactory.newInstance().newDocumentBuilder()
                                .newDocument());
                wordToHtmlConverter.processDocument(wordDocument);
                Document htmlDocument = wordToHtmlConverter.getDocument();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                DOMSource domSource = new DOMSource(htmlDocument);
                StreamResult streamResult = new StreamResult(out);

                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer serializer = tf.newTransformer();
                serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                serializer.setOutputProperty(OutputKeys.INDENT, "yes");
                serializer.setOutputProperty(OutputKeys.METHOD, "html");
                serializer.transform(domSource, streamResult);
                out.close();

                String result = new String(out.toByteArray());
                System.out.println(result);
                if(result.trim().equals("")){
                    throw new InvalidInputParamsException("File Content is Empty");
                }
                dtoHtml.setHtml(result);
            } catch (Exception e){
                System.out.println(e.getMessage());
                throw new InvalidInputParamsException("Error While Reading File");
            }
        }else if (ext.equalsIgnoreCase("docx")) {
            try {
                InputStream in =  new BufferedInputStream(file.getInputStream());
                XWPFDocument document = new XWPFDocument(in);
                XHTMLOptions options = XHTMLOptions.create().URIResolver(new FileURIResolver(new File("word/media")));
                OutputStream out = new ByteArrayOutputStream();
                XHTMLConverter.getInstance().convert(document, out, options);
                String html = out.toString();
                System.out.println(html);
                if(html.trim().equals("")){
                    throw new InvalidInputParamsException("File Content is Empty");
                }
                dtoHtml.setHtml(html);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new InvalidInputParamsException("Error While Reading File");
            }
        }else if (ext.equalsIgnoreCase("txt")) {
            try {
                ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
                String fileText = IOUtils.toString(stream, "UTF-8");
                if(fileText.trim().equals("")){
                    throw new InvalidInputParamsException("File Content is Empty");
                }
                String html = FileUtils.textToHTML(fileText);
                dtoHtml.setHtml(html);
            } catch (Exception e){
                System.out.println(e.getMessage());
                throw new InvalidInputParamsException("Error While Reading File");
            }
        } else {
            throw new InvalidInputParamsException("File Format Not Supported");
        }
            return new ResponseEntity<DtoHtml>(dtoHtml, HttpStatus.OK);
    }
}



