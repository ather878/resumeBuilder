package org.example.controller;

import org.example.model.ResumeEnhancementRequest;
import org.example.model.ResumeEnhancementResponse;
import org.example.service.OpenRouterResumeEnhancementService;
import org.example.service.PdfGenerationService;
import org.example.service.ResumeEnhancementService;
import org.example.util.PDFTextExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/resume")
public class ResumeEnhancementController {

    private final ResumeEnhancementService resumeEnhancementService;
    private final PDFTextExtractor pdfTextExtractor;
    private final PdfGenerationService pdfGenerationService;

    @Autowired
    public ResumeEnhancementController(OpenRouterResumeEnhancementService resumeEnhancementService,
                                     PDFTextExtractor pdfTextExtractor,
                                     PdfGenerationService pdfGenerationService) {
        this.resumeEnhancementService = resumeEnhancementService;
        this.pdfTextExtractor = pdfTextExtractor;
        this.pdfGenerationService = pdfGenerationService;
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
            
            // Process enhancement and generate PDF
            return resumeEnhancementService.enhanceResume(request)
                    .flatMap(response -> {
                        try {
                            // Generate PDF with enhanced resume
                            String baseFileName = "enhanced_resume";
                            String pdfPath = pdfGenerationService.generateAndStorePdf(
                                    response.getEnhancedResume(), 
                                    baseFileName
                            );
                            response.setPdfFilePath(pdfPath);
                            return Mono.just(ResponseEntity.ok(response));
                        } catch (IOException e) {
                            return Mono.just(ResponseEntity
                                    .internalServerError()
                                    .body(createErrorResponse("Error generating PDF: " + e.getMessage())));
                        }
                    })
                    .onErrorResume(e -> Mono.just(ResponseEntity
                            .internalServerError()
                            .body(createErrorResponse("Error processing resume: " + e.getMessage()))));
                    
        } catch (IOException e) {
            return Mono.just(ResponseEntity
                    .internalServerError()
                    .body(createErrorResponse("Error reading PDF file: " + e.getMessage())));
        }
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadPdf(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get("./generated-resumes").resolve(filename).normalize();
        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    private ResumeEnhancementResponse createErrorResponse(String errorMessage) {
        ResumeEnhancementResponse response = new ResumeEnhancementResponse();
        response.setStatus("ERROR");
        response.setSummaryOfChanges("Failed to enhance resume: " + errorMessage);
        response.setEnhancedResume("");
        return response;
    }
}
