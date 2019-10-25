package com.nbu.annotationplus.service;

import com.aspose.pdf.HtmlSaveOptions;
import com.aspose.pdf.LettersPositioningMethods;
import com.nbu.annotationplus.exception.InvalidInputParamsException;
import com.nbu.annotationplus.persistence.entity.User;
import com.nbu.annotationplus.persistence.repository.RoleRepository;
import com.nbu.annotationplus.persistence.repository.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.converter.WordToHtmlUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;

import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.fit.pdfdom.PDFDomTree;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserServiceTest {

    public static String textToHTML(String text) {
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
        System.out.println(out.toString());
        return out.toString();
    }






    @Test
    public void test1() {
        //MultipartFile file = "C:\\Users\\Lyudmila\\Desktop\\texttest.txt";
        //String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        //File file = new File("C:\\Users\\Lyudmila\\Desktop\\texttest.txt");

       /* try {
            //ByteArrayInputStream stream = new ByteArrayInputStream(file.getAbsolutePath());
            String myString = IOUtils.toString(stream, "UTF-8");
                /*BufferedReader br = new BufferedReader(new FileReader(file.getOriginalFilename()));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                String everything = sb.toString();

            if (myString.trim().equals("")) {
                throw new InvalidInputParamsException("File Content is Empty");
            }
        } catch (Exception e){

        }*/





    /*    String gosho = "";

       try {
            // System.out.println("dsdsds");
            File file = new File("C:\\Users\\Lyudmila\\Desktop\\texttest.txt");
           String s;

            BufferedReader br = new BufferedReader(new FileReader(file));

           // String s;
            while ((s = br.readLine()) != null){
                //System.out.println(s);
                StringBuffer builder = new StringBuffer();
                boolean previousWasASpace = false;
                for (char c : s.toCharArray()) {
                    if (c == ' ') {
                        if (previousWasASpace) {
                            builder.append("&nbsp;");
                            previousWasASpace = false;
                            continue;
                        }
                        previousWasASpace = true;
                    } else {
                        previousWasASpace = false;
                    }
                    switch (c) {
                        case '<':
                            System.out.println("pesho");
                            builder.append("&lt;");
                            break;
                        case '>':
                            System.out.println("pesho");
                            builder.append("&gt;");
                            break;
                        case '&':
                            System.out.println("pesho");
                            builder.append("&amp;");
                            break;
                        case '"':
                            System.out.println("pesho");
                            builder.append("&quot;");
                            break;
                        case '\n':
                            System.out.println("pesho");
                            builder.append("<br/>");
                            //builder += ("<br/>");
                            break;
                        case '\r' :
                            System.out.println("pesho");
                            builder.append("<br/>");
                            //builder += ("<br/>");
                            break;
                        // We need Tab support here, because we print StackTraces as HTML
                        case '\t':
                            System.out.println("pesho");
                            builder.append("&nbsp; &nbsp; &nbsp;");
                            break;
                        default:
                            if (c < 128) {
                                builder.append(c);
                            } else {
                                builder.append("&#").append((int) c).append(";");
                            }
                    }
                }
                System.out.println(builder.toString());
                // gosho += builder.toString();
                //textToHTML(builder.toString());
            }
            //System.out.println(gosho);
            //textToHTML(gosho);
            //return builder.toString();
            //System.out.println(builder.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

*/


/*

        com.aspose.pdf.Document pdfDocument = new com.aspose.pdf.Document("C:\\Users\\Lyudmila\\Desktop\\Test Files\\Sample-multi-pdf3.pdf");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //FileStream stream

// Instantiate HtmlSaveOptions instance
        HtmlSaveOptions newOptions = new HtmlSaveOptions();
        newOptions.PartsEmbeddingMode = HtmlSaveOptions.PartsEmbeddingModes.EmbedAllIntoHtml;

// This is just optimization for IE and can be omitted

        newOptions.LettersPositioningMethod = LettersPositioningMethods.UseEmUnitsAndCompensationOfRoundingErrorsInCss;
        newOptions.RasterImagesSavingMode = HtmlSaveOptions.RasterImagesSavingModes.AsEmbeddedPartsOfPngPageBackground;
        newOptions.FontSavingMode = HtmlSaveOptions.FontSavingModes.SaveInAllFormats;


// Specify the folder to save images during conversion process
// Save the resultant HTML file
        //pdfDocument.save("resultant.html", newOptions);
        pdfDocument.save(out,newOptions);
        System.out.println(out);
*/


/*try {
    Document doc = Jsoup.connect("http://www.1001recepti.com/s/208927-teleshka-kaima").get();
    System.out.println("dsdsd");
    Elements links = doc.select("link");
    Elements scripts = doc.select("style");
    for (Element element : links) {
        System.out.println(element.absUrl("src"));
    }
    for (Element element : scripts) {
        System.out.println(element.absUrl("src"));


    }
}catch (Exception e){
    System.out.println(e.getMessage());
}*/


/*try {
    System.out.println("blabla");
    URL oracle = new URL("http://www.oracle.com/");
    BufferedReader in = new BufferedReader(
            new InputStreamReader(oracle.openStream()));

    String inputLine;
    while ((inputLine = in.readLine()) != null)
        System.out.println(inputLine);
    in.close();
} catch (Exception e){
    System.out.println(e.getMessage());

}*/

     /*   try {
           // System.out.println("dsdsds");
            File file = new File("C:\\Users\\Lyudmila\\Desktop\\texttest.txt");

            BufferedReader br = new BufferedReader(new FileReader(file));

            String s;
            while ((s = br.readLine()) != null){
                //System.out.println(s);
                StringBuilder builder = new StringBuilder();
                boolean previousWasASpace = false;
                for (char c : s.toCharArray()) {
                    if (c == ' ') {
                        if (previousWasASpace) {
                            builder.append("&nbsp;");
                            previousWasASpace = false;
                            continue;
                        }
                        previousWasASpace = true;
                    } else {
                        previousWasASpace = false;
                    }
                    switch (c) {
                        case '<':
                             System.out.println("pesho");
                            builder.append("&lt;");
                            break;
                        case '>':
                             System.out.println("pesho");
                            builder.append("&gt;");
                            break;
                        case '&':
                             System.out.println("pesho");
                            builder.append("&amp;");
                            break;
                        case '"':
                            System.out.println("pesho");
                            builder.append("&quot;");
                            break;
                        case '\n':
                            System.out.println("pesho");
                            builder.append("<br/>");
                            //builder += ("<br/>");
                            break;
                        case '\r' :
                            System.out.println("pesho");
                            builder.append("<br/>");
                            //builder += ("<br/>");
                            break;
                        // We need Tab support here, because we print StackTraces as HTML
                        case '\t':
                            System.out.println("pesho");
                            builder.append("&nbsp; &nbsp; &nbsp;");
                            break;
                        default:
                            if (c < 128) {
                                builder.append(c);
                            } else {
                                builder.append("&#").append((int) c).append(";");
                            }
                    }
                }
                System.out.println(builder.toString());
            }
            //return builder.toString();
            //System.out.println(builder.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

*/



    /*




    try {

        PdfReader reader = new PdfReader(new FileInputStream(new File("C:\\Users\\lyubomir.semerdzhiev\\Desktop\\word-template.pdf")));

        for (int i = 0; i < reader.getXrefSize(); i++) {
            com.itextpdf.text.pdf.PdfObject pdfobj = reader.getPdfObject(i);
            if (pdfobj != null) {
                if (!pdfobj.isStream()) {
                    //throw new Exception("Not a stream");
                } else {
                    PdfStream stream = (PdfStream) pdfobj;
                    PdfObject pdfsubtype = stream.get(PdfName.SUBTYPE);
                    if (pdfsubtype == null) {
                        //  throw new Exception("Not an image stream");

                    } else {
                        if (!pdfsubtype.toString().equals(PdfName.IMAGE.toString())) {
                            //throw new Exception("Not an image stream");
                        } else {
                            // now you have a PDF stream object with an image
                            byte[] img = PdfReader.getStreamBytesRaw((PRStream) stream);
                            // but you don't know anything about the image format.
                            // you'll have to get info from the stream dictionary
                            System.out.println("----img ------");
                            System.out.println("height:" + stream.get(PdfName.HEIGHT));
                            System.out.println("width:" + stream.get(PdfName.WIDTH));
                            int height = new Integer(stream.get(PdfName.HEIGHT).toString()).intValue();
                            int width = new Integer(stream.get(PdfName.WIDTH).toString()).intValue();
                            System.out.println("bitspercomponent:" +
                                    stream.get(PdfName.BITSPERCOMPONENT));

                            java.awt.Image image = Toolkit.getDefaultToolkit().createImage(img);
                            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g2 = bi.createGraphics();
                            ImageIO.write(bi, "PNG", new File("C:\\Users\\lyubomir.semerdzhiev\\Desktop\\" + i + ".png"));
                        }

                    }
                }
//				...
//				// or you could try making a java.awt.Image from the array:
//				j

            }
        }
    } catch (Exception e){
        System.out.println(e.getMessage());
    }


}*/


        //AdminDashboardPage adminDashboardPage = new AdminDashboardPage();
        //adminDashboardPage.newDG();
        //DefineEditDataGroup defineEditDataGroup = new DefineEditDataGroup();
        // defineEditDataGroup.createDataGroup();
        //System.out.println("Starting...");
        //int test =  RestAssured.get("https://reqres.in/api/unknown/2").getStatusCode();
        //io.restassured.response.ResponseBody responseBody = RestAssured.get("https://reqres.in/api/unknown/2").getBody();
/*try {

   com.spire.pdf.PdfDocument pdf = new com.spire.pdf.PdfDocument();
    pdf.loadFromFile("C:\\Users\\lyubomir.semerdzhiev\\Desktop\\Merge_Before_Conversion.pdf");

//Save to HTML
    //pdf.saveToStream("ToHTML.html", FileFormat.HTML);
    //pdf.getBeforeSaveAction().getScript();
   // System.out.println(pdf.getBeforeSaveAction().getScript());
    pdf.saveToFile("ToHTML2.html", FileFormat.HTML);
    pdf.getConvertOptions().setPdfToHtmlOptions(true);
} catch (Exception e){
    System.out.println(e.getMessage());
}*/

// For complete examples and data files, please go to https://github.com/aspose-pdf/Aspose.Pdf-for-Java
// Load PDF document
       /* com.aspose.pdf.Document pdfDocument = new com.aspose.pdf.Document("C:\\Users\\lyubomir.semerdzhiev\\Desktop\\excerpts.pdf");
// Instantiate HtmlSaveOptions instance
        HtmlSaveOptions newOptions = new HtmlSaveOptions();
        newOptions.PartsEmbeddingMode = HtmlSaveOptions.PartsEmbeddingModes.EmbedAllIntoHtml;

// This is just optimization for IE and can be omitted
        newOptions.LettersPositioningMethod = LettersPositioningMethods.UseEmUnitsAndCompensationOfRoundingErrorsInCss;
        newOptions.RasterImagesSavingMode = HtmlSaveOptions.RasterImagesSavingModes.AsEmbeddedPartsOfPngPageBackground;
        newOptions.FontSavingMode = HtmlSaveOptions.FontSavingModes.SaveInAllFormats;
// Specify the folder to save images during conversion process
// Save the resultant HTML file
        pdfDocument.save("resultant.html", newOptions);*/
        // System.out.println(pdfDocument.get);


        //  static String readFile(String path, Charset encoding)
        //throws IOException
        //{
        // byte[] encoded = Files.readAllBytes(Paths.get(path));
        //  return new String(encoded, encoding);
        //}


      /*  try {
            String randomName = "pedsddsdsho333";

            PDDocument pdf = PDDocument.load(new File("C:\\Users\\lyubomir.semerdzhiev\\Desktop\\Merge_Before_Conversion.pdf"));
            Writer output = new PrintWriter("src/" + randomName + ".html", "utf-8");
            new PDFDomTree().writeText(pdf, output);
            //PDFBox pdfBox = PDFBox();
            //System.out.println(output);
            //File file = new File("src/" + randomName + ".html");
            //String test = file.toString();
            // byte[] encoded = Files.readAllBytes(Paths.get("src/" + randomName + ".html"));
            //new String(encoded, encoding);
            //System.out.println(encoded);
            //file.delete();
            output.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }*/

/*try {

    // load the PDF file using PDFBox
    PDDocument pdf = PDDocument.load(new File("C:\\Users\\lyubomir.semerdzhiev\\Desktop\\pdf-sample.pdf"));
// create the DOM parser
    PDFDomTree parser = new PDFDomTree();
// parse the file and get the DOM Document
    Document dom = parser.createDOM(pdf);
    //System.out.println(dom.getDoctype());

} catch (Exception e){
    System.out.println(e.getMessage());
}*/
        //System.out.println(test);
        //System.out.println(responseBody.asString());



/*try {

    HWPFDocumentCore wordDocument = WordToHtmlUtils.loadDoc(new FileInputStream("C:\\Users\\Lyudmila\\Desktop\\F69593.doc"));
    //OPCPackage wordDocument = OPCPackage.open(new File("C:\\Users\\lyubomir.semerdzhiev\\Desktop\\demo.docx"));
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
} catch (Exception e){
    System.out.println(e.getMessage());
}*/



/*        try {
            InputStream in = new FileInputStream(new File("C:\\Users\\Lyudmila\\Desktop\\tag-example.docx"));
            XWPFDocument document = new XWPFDocument(in);

            XHTMLOptions options = XHTMLOptions.create().URIResolver(new FileURIResolver(new File("word/media")));

            OutputStream out = new ByteArrayOutputStream();

            XHTMLConverter.getInstance().convert(document, out, options);
            String html = out.toString();
            System.out.println(html);
            //dtoHtml.setHtml(html);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }*/


/*try {
    URL url = new URL("https://www.lipsum.com");
    URLConnection con = url.openConnection();
    InputStream in = con.getInputStream();
    String encoding = con.getContentEncoding();  // ** WRONG: should use "con.getContentType()" instead but it returns something like "text/html; charset=UTF-8" so this value must be parsed to extract the actual encoding
    encoding = encoding == null ? "UTF-8" : encoding;
    String body = IOUtils.toString(in, encoding);
    System.out.println(body);
}catch (Exception e){
    System.out.println(e.getMessage());
}

    }


    }*/

       // String html = "<p>An <a href='http://example.com/'><b>example</b></a> link.</p>";
       // Document doc = Jsoup.parse(html);
       // String text = doc.body().text();

        try {
            Document document = Jsoup.connect("https://stackoverflow.com/questions/9825798/how-to-read-a-text-from-a-web-page-with-java").get();
            String html = document.body().html();
            Document doc2 = Jsoup.parse(html);
            //System.out.println(html);
            System.out.println(document.toString());
            //String text = doc2.body().html();
            //System.out.println(text);
        } catch (Exception e){

        }
    }
}







