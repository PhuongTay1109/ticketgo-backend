package com.ticketgo.repository;

import com.ticketgo.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}
