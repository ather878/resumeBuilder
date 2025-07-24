package org.example.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class PDFTextExtractor {

    /**
     * Extracts text from a PDF file
     * @param file The PDF file to extract text from
     * @return Extracted text as a String
     * @throws IOException If there's an error reading the PDF file
     */
    public String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            if (document.isEncrypted()) {
                // Try to decrypt with an empty password
                document.setAllSecurityToBeRemoved(true);
            }
            
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    
    /**
     * Checks if the file is a PDF
     * @param file The file to check
     * @return true if the file is a PDF, false otherwise
     */
    public boolean isPdfFile(MultipartFile file) {
        return file != null && 
               file.getContentType() != null && 
               file.getContentType().equals("application/pdf");
    }
}
