package org.example.controller;

import org.example.model.ResumeEnhancementRequest;
import org.example.model.ResumeEnhancementResponse;
import org.example.service.OpenRouterResumeEnhancementService;
import org.example.service.ResumeEnhancementService;
import org.example.util.PDFTextExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequestMapping("/api/resume")
public class ResumeEnhancementController {

    private final ResumeEnhancementService resumeEnhancementService;

    private final PDFTextExtractor pdfTextExtractor;

    @Autowired
    public ResumeEnhancementController(OpenRouterResumeEnhancementService resumeEnhancementService,
                                       PDFTextExtractor pdfTextExtractor) {
        this.resumeEnhancementService = resumeEnhancementService;
        this.pdfTextExtractor = pdfTextExtractor;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "Return Hello";
    }

    @PostMapping("/enhance")
    public Mono<ResponseEntity<ResumeEnhancementResponse>> enhanceResume(
            @RequestBody ResumeEnhancementRequest request) {
        return resumeEnhancementService.enhanceResume(request)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity
                        .internalServerError()
                        .body(createErrorResponse(e.getMessage()))));
    }
    
    @PostMapping(value = "/enhance/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ResumeEnhancementResponse>> enhanceResumeFromPdf(
            @RequestParam("resume") MultipartFile resumeFile,
            @RequestParam("jobDescription") String jobDescription,
            @RequestParam(value = "instructions", required = false) String instructions) {
        
        try {
            // Validate file
            if (resumeFile.isEmpty()) {
                return Mono.just(ResponseEntity.badRequest()
                        .body(createErrorResponse("Resume file is required")));
            }

            if (!pdfTextExtractor.isPdfFile(resumeFile)) {
                return Mono.just(ResponseEntity.badRequest()
                        .body(createErrorResponse("Only PDF files are supported")));
            }

            // Extract text from PDF
            String resumeText = pdfTextExtractor.extractTextFromPdf(resumeFile);
            
            // Create enhancement request
            ResumeEnhancementRequest request = new ResumeEnhancementRequest();
            request.setOriginalResume(resumeText);
            request.setJobDescription(jobDescription);
            request.setEnhancementInstructions(instructions != null ? instructions : "");
            
            // Process enhancement
            return resumeEnhancementService.enhanceResume(request)
                    .map(ResponseEntity::ok)
                    .onErrorResume(e -> Mono.just(ResponseEntity
                            .internalServerError()
                            .body(createErrorResponse("Error processing PDF: " + e.getMessage()))));
                    
        } catch (IOException e) {
            return Mono.just(ResponseEntity
                    .internalServerError()
                    .body(createErrorResponse("Error reading PDF file: " + e.getMessage())));
        }
    }

    private ResumeEnhancementResponse createErrorResponse(String errorMessage) {
        ResumeEnhancementResponse response = new ResumeEnhancementResponse();
        response.setStatus("ERROR");
        response.setSummaryOfChanges("Failed to enhance resume: " + errorMessage);
        response.setEnhancedResume("");
        return response;
    }
}
