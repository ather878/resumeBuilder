package org.example.model;

public class ResumeEnhancementRequest {
    private String originalResume;
    private String jobDescription;
    private String enhancementInstructions;

    // Getters and Setters
    public String getOriginalResume() {
        return originalResume;
    }

    public void setOriginalResume(String originalResume) {
        this.originalResume = originalResume;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getEnhancementInstructions() {
        return enhancementInstructions;
    }

    public void setEnhancementInstructions(String enhancementInstructions) {
        this.enhancementInstructions = enhancementInstructions;
    }
}
