package com.ticketgo.repository;

import com.ticketgo.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("""
       SELECT m
       FROM Message m
       WHERE (m.sender.userId = :senderId AND m.receiver.userId = :receiverId)
            OR (m.sender.userId = :receiverId AND m.receiver.userId = :senderId)
       ORDER BY m.createdAt ASC
    """)
    List<Message> findAllBySenderIdAndReceiverId(Long senderId, Long receiverId);

    @Query("""
        SELECT DISTINCT 
            CASE WHEN m.sender.userId = :userId THEN m.receiver.userId ELSE m.sender.userId END
        FROM Message m
        WHERE m.sender.userId = :userId OR m.receiver.userId = :userId
    """)
    List<Long> findChatPartnerIds(@Param("userId") Long userId);

    @Query("""
        SELECT m FROM Message m
        WHERE (m.sender.userId = :userId1 AND m.receiver.userId = :userId2)
           OR (m.sender.userId = :userId2 AND m.receiver.userId = :userId1)
        ORDER BY m.sentAt DESC
    """)
    List<Message> findConversationMessages(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
