package com.recruitment.app.reports;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.recruitment.app.models.RecruitmentReport;

import java.io.FileOutputStream;

public class PDFReportExporter implements IReportExporter {

    @Override
    public void export(RecruitmentReport report, String filepath) throws Exception {

        Document doc = new Document(); // Correct Document from iText
        PdfWriter.getInstance(doc, new FileOutputStream(filepath));
        doc.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font fieldFont = new Font(Font.FontFamily.HELVETICA, 12);

        doc.add(new Paragraph("Recruitment Report", titleFont));
        doc.add(new Paragraph("Generated On: " + report.getGeneratedAt(), fieldFont));
        doc.add(new Paragraph("Job ID: " + report.getJobId(), fieldFont));
        doc.add(new Paragraph("Recruiter ID: " + report.getRecruiterId(), fieldFont));
        doc.add(new Paragraph("\n"));

        doc.add(new Paragraph("Total Applications: " + report.getTotalApplications(), fieldFont));
        doc.add(new Paragraph("Total Shortlisted: " + report.getTotalShortlisted(), fieldFont));

        doc.close();
    }
}
