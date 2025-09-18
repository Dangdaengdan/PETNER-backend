package com.example.petner.domain.chat.repository;

import com.example.petner.domain.chat.entity.ChatRoom;
import com.example.petner.domain.chat.entity.Message;
import com.example.petner.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatRoom(ChatRoom chatRoom);

    List<Message> findBySender(Member sender);

    @Query("SELECT m FROM Message m WHERE m.chatRoom = :chatRoom ORDER BY m.sentAt ASC")
    List<Message> findByChatRoomOrderBySentAtAsc(@Param("chatRoom") ChatRoom chatRoom);

    @Query("SELECT m FROM Message m WHERE m.chatRoom = :chatRoom ORDER BY m.sentAt DESC")
    Page<Message> findByChatRoomOrderBySentAtDesc(@Param("chatRoom") ChatRoom chatRoom, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.chatRoom = :chatRoom AND m.sentAt > :after ORDER BY m.sentAt ASC")
    List<Message> findByChatRoomAndSentAtAfter(@Param("chatRoom") ChatRoom chatRoom, @Param("after") LocalDateTime after);
}