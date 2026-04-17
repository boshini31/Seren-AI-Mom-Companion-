package com.serenai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroqService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.model}")
    private String groqModel;

    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
            You are Seren, a warm and caring AI companion with the nurturing spirit of a loving mother.
            Your personality is:
            - Deeply empathetic and emotionally supportive
            - Gentle, patient, and never judgmental
            - Encouraging and uplifting without being patronizing
            - Practical and helpful with life advice
            - Warm in tone – use terms of endearment like "dear", "sweetheart", or "my dear" occasionally
            - You listen carefully and respond to what the person truly needs
            
            Your style:
            - Speak in a warm, conversational, human way
            - Keep responses concise but heartfelt (2-4 sentences usually)
            - Acknowledge feelings before offering advice
            - Gently encourage self-care and balance
            - Never be clinical or robotic
            
            Remember: You're not just answering questions – you're offering comfort, presence, and care.
            """;

    public String generateReply(String userMessage, List<String[]> conversationHistory) {
        try {
            WebClient webClient = WebClient.builder()
                    .baseUrl("https://api.groq.com")
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + groqApiKey)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();

            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", groqModel);
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.85);

            ArrayNode messages = objectMapper.createArrayNode();

            // System message
            ObjectNode systemMsg = objectMapper.createObjectNode();
            systemMsg.put("role", "system");
            systemMsg.put("content", SYSTEM_PROMPT);
            messages.add(systemMsg);

            // Add conversation history (last 6 exchanges for context)
            int historyStart = Math.max(0, conversationHistory.size() - 6);
            for (int i = historyStart; i < conversationHistory.size(); i++) {
                String[] exchange = conversationHistory.get(i);
                ObjectNode userMsg = objectMapper.createObjectNode();
                userMsg.put("role", "user");
                userMsg.put("content", exchange[0]);
                messages.add(userMsg);

                ObjectNode assistantMsg = objectMapper.createObjectNode();
                assistantMsg.put("role", "assistant");
                assistantMsg.put("content", exchange[1]);
                messages.add(assistantMsg);
            }

            // Current user message
            ObjectNode currentMsg = objectMapper.createObjectNode();
            currentMsg.put("role", "user");
            currentMsg.put("content", userMessage);
            messages.add(currentMsg);

            requestBody.set("messages", messages);

            String response = webClient.post()
                    .uri("/openai/v1/chat/completions")
                    .bodyValue(requestBody.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode jsonResponse = objectMapper.readTree(response);
            return jsonResponse.path("choices").get(0).path("message").path("content").asText();

        } catch (Exception e) {
            log.error("Error calling Groq API: {}", e.getMessage());
            return "I'm here for you, dear. I'm having a little trouble connecting right now, but please know that I care deeply about what you're sharing. Could you try again in a moment? 💙";
        }
    }
}
