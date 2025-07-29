package org.example.model;

public class ResumeEnhancementResponse {
    private String enhancedResume;
    private String summaryOfChanges;
    private String status;
    private String pdfFilePath;

    // Getters and Setters
    public String getEnhancedResume() {
        return enhancedResume;
    }

    public void setEnhancedResume(String enhancedResume) {
        this.enhancedResume = enhancedResume;
    }

    public String getSummaryOfChanges() {
        return summaryOfChanges;
    }

    public void setSummaryOfChanges(String summaryOfChanges) {
        this.summaryOfChanges = summaryOfChanges;
    }

    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public String getPdfFilePath() {
        return pdfFilePath;
    }

    public void setPdfFilePath(String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
    }
}
