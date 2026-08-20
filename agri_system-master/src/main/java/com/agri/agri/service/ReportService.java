package com.agri.agri.service;

import com.agri.agri.entity.ReportLog;
import com.agri.agri.entity.User;
import com.agri.agri.repository.ReportLogRepository;
import com.agri.agri.repository.UserRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final UserRepository userRepository;
    private final ReportLogRepository reportLogRepository;

    public ReportService(UserRepository userRepository, ReportLogRepository reportLogRepository) {
        this.userRepository = userRepository;
        this.reportLogRepository = reportLogRepository;
    }

    @Transactional
    public void exportVerifiedUsersToPdf(HttpServletResponse response, String role) throws IOException {
        //Fetch Verified Users
        List<User> users = userRepository.findByRoleAndIsVerified(role.toUpperCase(), true);

        //Setup PDF Document
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        //Add Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Verified " + role + "s Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        //Create Table (ID, Username, Email)
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{1.5f, 3.5f, 5.0f});

        //Table Headers
        PdfPCell cell = new PdfPCell();
        cell.setPadding(6);
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

        cell.setPhrase(new Phrase("User ID", headFont)); table.addCell(cell);
        cell.setPhrase(new Phrase("Username", headFont)); table.addCell(cell);
        cell.setPhrase(new Phrase("Email", headFont)); table.addCell(cell);

        //Populate Table Rows
        for (User user : users) {
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getUsername());
            table.addCell(user.getEmail());
        }
        document.add(table);
        document.close();

        //Log the report generation in the database
        ReportLog log = new ReportLog(role.toUpperCase() + " PDF REPORT", LocalDateTime.now());
        reportLogRepository.save(log);
    }
}