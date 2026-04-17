package com.serenai.service;

import com.serenai.dto.ChatDto;
import com.serenai.model.Conversation;
import com.serenai.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final GroqService groqService;
    private final ConversationRepository conversationRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ChatDto.ChatResponse processMessage(ChatDto.ChatRequest request) {
        String sessionId = (request.getSessionId() == null || request.getSessionId().isBlank())
                ? UUID.randomUUID().toString()
                : request.getSessionId();

        // Fetch recent history for context
        List<Conversation> history = conversationRepository
                .findTop10BySessionIdOrderByCreatedAtDesc(sessionId);

        // Reverse to chronological order for the prompt
        List<String[]> historyPairs = history.stream()
                .sorted((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()))
                .map(c -> new String[]{c.getUserMessage(), c.getAiReply()})
                .collect(Collectors.toList());

        // Call Groq AI
        String aiReply = groqService.generateReply(request.getMessage(), historyPairs);

        // Save conversation
        Conversation saved = conversationRepository.save(
                Conversation.builder()
                        .sessionId(sessionId)
                        .userMessage(request.getMessage())
                        .aiReply(aiReply)
                        .build()
        );

        log.debug("Saved conversation id={}, session={}", saved.getId(), sessionId);
        return new ChatDto.ChatResponse(aiReply, sessionId, saved.getId());
    }

    public List<ChatDto.ConversationHistoryItem> getHistory(String sessionId) {
        return conversationRepository
                .findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(c -> new ChatDto.ConversationHistoryItem(
                        c.getId(),
                        c.getUserMessage(),
                        c.getAiReply(),
                        c.getCreatedAt().format(FORMATTER)
                ))
                .collect(Collectors.toList());
    }

    public void clearHistory(String sessionId) {
        List<Conversation> conversations = conversationRepository
                .findBySessionIdOrderByCreatedAtAsc(sessionId);
        conversationRepository.deleteAll(conversations);
        log.debug("Cleared history for session={}", sessionId);
    }
}
