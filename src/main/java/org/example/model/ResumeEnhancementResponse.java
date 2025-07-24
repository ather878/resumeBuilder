package org.example.model;

public class ResumeEnhancementResponse {
    private String enhancedResume;
    private String summaryOfChanges;
    private String status;

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
}
