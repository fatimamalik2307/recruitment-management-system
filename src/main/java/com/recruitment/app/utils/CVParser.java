package com.recruitment.app.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;

public class CVParser {

    public static String extractText(File file) {
        try {
            String name = file.getName().toLowerCase();

            if (name.endsWith(".pdf")) {
                return readPDF(file);
            } else if (name.endsWith(".docx")) {
                return readDOCX(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String readPDF(File file) throws Exception {
        PDDocument doc = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(doc);
        doc.close();
        return text;
    }

    private static String readDOCX(File file) throws Exception {
        XWPFDocument doc = new XWPFDocument(new FileInputStream(file));
        StringBuilder sb = new StringBuilder();
        for (XWPFParagraph p : doc.getParagraphs()) {
            sb.append(p.getText()).append("\n");
        }
        doc.close();
        return sb.toString();
    }

    // ------------------------------
    // Extract specific fields
    // ------------------------------

    public static String extractEmail(String text) {
        return text.replaceAll("(?i).*?([A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}).*", "$1");
    }

    public static String extractPhone(String text) {
        return text.replaceAll("(?s).*?(\\+?\\d{2,4}[\\s-]?\\d{3}[\\s-]?\\d{3}[\\s-]?\\d{3,4}).*", "$1");
    }
}
