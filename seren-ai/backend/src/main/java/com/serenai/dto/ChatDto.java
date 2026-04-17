package com.serenai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class ChatDto {

    @Data
    public static class ChatRequest {
        @NotBlank(message = "Message cannot be empty")
        private String message;

        private String sessionId;
    }

    @Data
    public static class ChatResponse {
        private String reply;
        private String sessionId;
        private Long conversationId;

        public ChatResponse(String reply, String sessionId, Long conversationId) {
            this.reply = reply;
            this.sessionId = sessionId;
            this.conversationId = conversationId;
        }
    }

    @Data
    public static class ConversationHistoryItem {
        private Long id;
        private String userMessage;
        private String aiReply;
        private String createdAt;

        public ConversationHistoryItem(Long id, String userMessage, String aiReply, String createdAt) {
            this.id = id;
            this.userMessage = userMessage;
            this.aiReply = aiReply;
            this.createdAt = createdAt;
        }
    }
}
