package com.serenai.controller;

import com.serenai.dto.ChatDto;
import com.serenai.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * POST /api/chat – Send a message and get Seren's reply
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatDto.ChatResponse> chat(@Valid @RequestBody ChatDto.ChatRequest request) {
        log.debug("Received message: {}", request.getMessage());
        ChatDto.ChatResponse response = chatService.processMessage(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/history/{sessionId} – Get conversation history for a session
     */
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<ChatDto.ConversationHistoryItem>> getHistory(@PathVariable String sessionId) {
        return ResponseEntity.ok(chatService.getHistory(sessionId));
    }

    /**
     * DELETE /api/history/{sessionId} – Clear conversation history
     */
    @DeleteMapping("/history/{sessionId}")
    public ResponseEntity<Map<String, String>> clearHistory(@PathVariable String sessionId) {
        chatService.clearHistory(sessionId);
        return ResponseEntity.ok(Map.of("message", "Conversation history cleared."));
    }

    /**
     * GET /api/health – Simple health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "OK", "service", "Seren AI"));
    }
}
