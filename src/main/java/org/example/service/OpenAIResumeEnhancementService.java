package org.example.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.example.model.ResumeEnhancementRequest;
import org.example.model.ResumeEnhancementResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAIResumeEnhancementService implements ResumeEnhancementService {

    private static final String MODEL = "gpt-4";
    private final OpenAiService openAiService;

    @Autowired
    public OpenAIResumeEnhancementService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @Override
    public Mono<ResumeEnhancementResponse> enhanceResume(ResumeEnhancementRequest request) {
        return Mono.fromCallable(() -> {
            String prompt = createEnhancementPrompt(request);
            
            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), 
                "You are a professional resume writer and career coach. Your task is to enhance a resume to better match a job description while maintaining the original content's integrity."));
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompt));

            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(MODEL)
                .messages(messages)
                .temperature(0.7)
                .maxTokens(2000)
                .build();

            String enhancedResume = openAiService.createChatCompletion(completionRequest)
                .getChoices().get(0).getMessage().getContent();

            ResumeEnhancementResponse response = new ResumeEnhancementResponse();
            response.setEnhancedResume(enhancedResume);
            response.setStatus("SUCCESS");
            response.setSummaryOfChanges("Resume has been enhanced to better match the job description.");
            
            return response;
        });
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
