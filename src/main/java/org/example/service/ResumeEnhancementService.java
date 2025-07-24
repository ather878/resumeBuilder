package org.example.service;

import org.example.model.ResumeEnhancementRequest;
import org.example.model.ResumeEnhancementResponse;
import reactor.core.publisher.Mono;

public interface ResumeEnhancementService {
    Mono<ResumeEnhancementResponse> enhanceResume(ResumeEnhancementRequest request);
}
