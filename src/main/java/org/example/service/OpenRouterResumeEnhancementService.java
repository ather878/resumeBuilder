package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.ResumeEnhancementRequest;
import org.example.model.ResumeEnhancementResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenRouterResumeEnhancementService implements ResumeEnhancementService {

    private static final String OPENROUTER_API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "deepseek/deepseek-chat-v3-0324:free";
    
    private final WebClient webClient;
    private final String apiKey;

    @Autowired
    public OpenRouterResumeEnhancementService(@Value("${openrouter.api.key}") String apiKey, WebClient.Builder webClientBuilder) {
        this.apiKey = apiKey;
        this.webClient = webClientBuilder
                .baseUrl(OPENROUTER_API_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader("HTTP-Referer", "https://github.com/yourusername/resume-builder")
                .defaultHeader("X-Title", "Resume Builder")
                .build();
    }

    @Override
    public Mono<ResumeEnhancementResponse> enhanceResume(ResumeEnhancementRequest request) {
        return Mono.fromCallable(() -> createChatCompletionRequest(request))
                .flatMap(this::callOpenRouterApi)
                .map(this::mapToResponse);
    }

    private Map<String, Object> createChatCompletionRequest(ResumeEnhancementRequest request) {
        String prompt = createEnhancementPrompt(request);
        
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
            "role", "system",
            "content", "You are a professional resume writer and career coach. Your task is to enhance a resume to better match a job description while maintaining the original content's integrity."
        ));
        messages.add(Map.of(
            "role", "user",
            "content", prompt
        ));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", MODEL);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2000);
        
        return requestBody;
    }

    private Mono<Map> callOpenRouterApi(Map<String, Object> requestBody) {
        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class);
    }

    private ResumeEnhancementResponse mapToResponse(Map response) {
        ResumeEnhancementResponse enhancementResponse = new ResumeEnhancementResponse();
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                String content = (String) message.get("content");
                enhancementResponse.setEnhancedResume(content);
                enhancementResponse.setStatus("SUCCESS");
                enhancementResponse.setSummaryOfChanges("Resume has been enhanced to better match the job description.");
            } else {
                throw new RuntimeException("No choices in response");
            }
        } catch (Exception e) {
            enhancementResponse.setStatus("ERROR");
            enhancementResponse.setSummaryOfChanges("Failed to process response from OpenRouter: " + e.getMessage());
            enhancementResponse.setEnhancedResume("");
        }
        return enhancementResponse;
    }

    private String createEnhancementPrompt(ResumeEnhancementRequest request) {
        return String.format("""
            Please enhance the following resume to better match the job description below.
            Focus on:
            1. Highlighting relevant skills and experiences
            2. Using keywords from the job description
            3. Maintaining a professional tone
            4. Keeping the format clean and consistent
            
            === ORIGINAL RESUME ===
            %s
            
            === JOB DESCRIPTION ===
            %s
            
            === ADDITIONAL INSTRUCTIONS ===
            %s
            
            Please return the enhanced resume with a summary of changes at the top.
            """, 
            request.getOriginalResume(), 
            request.getJobDescription(),
            request.getEnhancementInstructions() != null ? request.getEnhancementInstructions() : "None");
    }
}
