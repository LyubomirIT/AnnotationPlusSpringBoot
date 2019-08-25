package com.nbu.annotationplus.service;

import com.nbu.annotationplus.dto.DtoHtml;
import com.nbu.annotationplus.exception.InvalidInputParamsException;

import com.nbu.annotationplus.utils.ParseUtils;
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
    private static String textToHTML(String text) {
        if(text == null) {
            return null;
        }
        int length = text.length();
        boolean prevSlashR = false;
        StringBuffer out = new StringBuffer();
        for(int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            switch(ch) {
                case '\r':
                    if(prevSlashR) {
                        out.append("<br>");
                    }
                    prevSlashR = true;
                    break;
                case '\n':
                    prevSlashR = false;
                    out.append("<br>");
                    break;
                case '"':
                    if(prevSlashR) {
                        out.append("<br>");
                        prevSlashR = false;
                    }
                    out.append("&quot;");
                    break;
                case ' ':
                    if(prevSlashR) {
                        out.append("<br>");
                        prevSlashR = false;
                    }
                    out.append("&nbsp;");
                    break;
                case '<':
                    if(prevSlashR) {
                        out.append("<br>");
                        prevSlashR = false;
                    }
                    out.append("&lt;");
                    break;
                case '>':
                    if(prevSlashR) {
                        out.append("<br>");
                        prevSlashR = false;
                    }
                    out.append("&gt;");
                    break;
                case '&':
                    if(prevSlashR) {
                        out.append("<br>");
                        prevSlashR = false;
                    }
                    out.append("&amp;");
                    break;
                default:
                    if(prevSlashR) {
                        out.append("<br>");
                        prevSlashR = false;
                    }
                    out.append(ch);
                    break;
            }
        }
        //System.out.println(out.toString());
        return out.toString();
    }







    @Transactional
    public ResponseEntity<DtoHtml> readFile(MultipartFile file) {
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        if(ext == null){
            throw new InvalidInputParamsException("Invalid File");
        }
       if(file.getSize() > ParseUtils.MAX_FILE_SIZE_IN_BYTES){
           throw new InvalidInputParamsException("File is too big. Max file size is 5 MB");
        }
        DtoHtml dtoHtml = new DtoHtml();
        if (ext.equalsIgnoreCase("pdf")) {
            //StringBuilder contentBuilder = new StringBuilder();
            //String content;
            try {
                InputStream inputStream = new BufferedInputStream(file.getInputStream());
                StringWriter out = new StringWriter();

                // File convFile = new File(file.getOriginalFilename());
                // convFile.createNewFile();
                // FileOutputStream fos = new FileOutputStream(convFile);
                // fos.write(file.getBytes());
                //  fos.close();
                //file.transferTo(convFile);
                //System.out.println(file.toString());
                // String ext = FilenameUtils.getExtension(convFile);
                //System.out.println(file.getInputStream());
                //System.out.println(file.getBytes());
                // System.out.println(file.getName());
                // System.out.println(file.getOriginalFilename());
                // System.out.println(convFile.getName());
                // System.out.println(convFile.getAbsolutePath());
                PDDocument pdf = PDDocument.load(inputStream);
                //PDDocument pdf = PDDocument.load(new File(dtoFile.getFile()));
                //Writer output = new PrintWriter("testtestov.html", "utf-8");
                Writer output = new PrintWriter(out, true);
                //PrintWriter output = new PrintWriter(file.getInputStream());
                new PDFDomTree().writeText(pdf, output);
                //System.out.println("Output " + out.toString());
                output.close();
                //System.out.println("Output " + output.toString());
                //output.flush();
               /* BufferedReader in = new BufferedReader(new FileReader("src/" + randomName + ".html"));
                String str;
                while ((str = in.readLine()) != null) {
                    contentBuilder.append(str);
                }
                in.close();
                content = contentBuilder.toString();
                System.out.println(content);
                if(content.trim().equals("")){
                    throw new InvalidInputParamsException("File Content is Empty");
                }*/
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
               // htmlDocument.getp
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                DOMSource domSource = new DOMSource(htmlDocument);
                StreamResult streamResult = new StreamResult(out);

                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer serializer = tf.newTransformer();
                serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                //serializer.setOutputProperty(OutputKeys.INDENT, "no");
                serializer.setOutputProperty(OutputKeys.INDENT, "yes");
               // serializer.setOutputProperty(OutputKeys. "yes");
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
                String myString = IOUtils.toString(stream, "UTF-8");
                System.out.println(myString);
                String novo = "";
                novo = textToHTML(myString);
                System.out.println(novo);
                /*BufferedReader br = new BufferedReader(new FileReader(file.getOriginalFilename()));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                String everything = sb.toString();
                */if(myString.trim().equals("")){
                    throw new InvalidInputParamsException("File Content is Empty");
                }
                dtoHtml.setHtml(novo);
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        } else {
            throw new InvalidInputParamsException("File Format Not Supported");
        }
            return new ResponseEntity<DtoHtml>(dtoHtml, HttpStatus.OK);
    }
/*
    @Transactional
    public ResponseEntity<DtoHtml> convertWord(MultipartFile file) {
        DtoHtml dtoHtml = new DtoHtml();
        try {
           // InputStream in = new FileInputStream(new File("C:\\Users\\lyubomir.semerdzhiev\\Desktop\\docx.docx"));
            //InputStream in = new FileInputStream(new File(file.getInputStream()));
            InputStream in =  new BufferedInputStream(file.getInputStream());
            XWPFDocument document = new XWPFDocument(in);

            XHTMLOptions options = XHTMLOptions.create().URIResolver(new FileURIResolver(new File("word/media")));

            OutputStream out = new ByteArrayOutputStream();

            XHTMLConverter.getInstance().convert(document, out, options);
            String html = out.toString();
            System.out.println(html);
            dtoHtml.setHtml(html);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new InvalidInputParamsException("Error While Reading File");
        }
        return new ResponseEntity<DtoHtml>(dtoHtml, HttpStatus.OK);
    }



    @Transactional
    public ResponseEntity<DtoHtml> convertWord2(MultipartFile file){
        DtoHtml dtoHtml = new DtoHtml();
        try {
            HWPFDocumentCore wordDocument = WordToHtmlUtils.loadDoc(new BufferedInputStream(file.getInputStream()));
            //OPCPackage wordDocument = OPCPackage.open(new File("C:\\Users\\lyubomir.semerdzhiev\\Desktop\\demo.doc"));
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
           // serializer.setOutputProperty(OutputStream., "UTF-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");
            serializer.transform(domSource, streamResult);
            out.close();

            String result = new String(out.toByteArray());
            System.out.println(result);
            //dtoHtml.setHtml(result);
            //HtmlUtils.htmlUnescape(result);
            dtoHtml.setHtml(result);
        } catch (Exception e){
            System.out.println(e.getMessage());
            throw new InvalidInputParamsException("Error While Reading File");
        }
        return new ResponseEntity<DtoHtml>(dtoHtml, HttpStatus.OK);
    }


    @Transactional
    public ResponseEntity<DtoHtml> convertToTxt(MultipartFile file){
        DtoHtml dtoHtml = new DtoHtml();

        try {
            //BufferedReader br = new BufferedReader(new BufferedInputStream(file.getInputStream()));
            BufferedReader br = new BufferedReader(new FileReader(file.getName()));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            dtoHtml.setHtml(everything);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<DtoHtml>(dtoHtml, HttpStatus.OK);
    }
*/
   /* private String getFileContent() {
        String randomName = "peshoooooo";
        try{
        StringBuilder contentBuilder = new StringBuilder();
        String content;
        BufferedReader in = new BufferedReader(new FileReader("src/" + randomName + ".html"));
        String str;
        while ((str = in.readLine()) != null) {
            contentBuilder.append(str);
        }
        in.close();
        content = contentBuilder.toString();
        System.out.println(content);
        return content;
    } catch(Exception e) {
            throw new InvalidInputParamsException("Error While Reading File");
        }
    }*/
}



