package com.ticketgo.repository;

import com.ticketgo.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("SELECT m FROM ChatMessage m WHERE m.conversation.conversationId = :conversationId ORDER BY m.createdAt ASC")
    List<ChatMessage> findByConversationIdOrderByCreatedAtAsc(@Param("conversationId") String conversationId);
}
