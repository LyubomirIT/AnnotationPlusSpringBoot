package com.nbu.annotationplus.utils;

public class FileUtils {

    public static final int MAX_FILE_SIZE_IN_BYTES = 5242880; // 5MB

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
        return out.toString();
    }
}
