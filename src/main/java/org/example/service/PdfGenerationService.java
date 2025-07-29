package org.example.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGenerationService {

    @Value("${app.storage.directory:./generated-resumes}")
    private String storageDirectory;

    public String generateAndStorePdf(String content, String baseFileName) throws IOException {
        // Create storage directory if it doesn't exist
        Path storagePath = Paths.get(storageDirectory);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        // Generate unique filename with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = String.format("%s_%s.pdf", baseFileName, timestamp);
        Path filePath = storagePath.resolve(fileName);

        // Generate PDF
        try (PdfWriter writer = new PdfWriter(filePath.toFile());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            
            // Add content to PDF
            document.add(new Paragraph("Enhanced Resume"));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(content));
        }

        return filePath.toString();
    }

    public File getPdfFile(String filePath) {
        File file = new File(filePath);
        return file.exists() ? file : null;
    }
}
