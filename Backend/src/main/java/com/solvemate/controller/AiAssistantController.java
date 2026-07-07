package com.solvemate.controller;

import com.solvemate.dto.AiChatRequest;
import com.solvemate.dto.AiChatResponse;
import com.solvemate.service.AiAssistantService;
import com.solvemate.service.impl.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiAssistantController {

    private final AiAssistantService aiAssistantService;
    private final GeminiService geminiService;

    public AiAssistantController(AiAssistantService aiAssistantService, GeminiService geminiService) {
        this.aiAssistantService = aiAssistantService;
        this.geminiService      = geminiService;
    }

    @GetMapping("/test")
    public String test() {
        return geminiService.ask("Say hello in one short sentence and confirm you are working.");
    }

    @PostMapping("/ask")
    public ResponseEntity<AiChatResponse> ask(@RequestBody AiChatRequest request) {
        String answer = aiAssistantService.answer(request.getPolymerId(), request.getQuestion());
        return ResponseEntity.ok(new AiChatResponse(answer));
    }
}