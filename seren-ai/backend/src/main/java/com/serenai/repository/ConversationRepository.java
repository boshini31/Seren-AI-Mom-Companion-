package com.serenai.repository;

import com.serenai.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    List<Conversation> findTop10BySessionIdOrderByCreatedAtDesc(String sessionId);
}
