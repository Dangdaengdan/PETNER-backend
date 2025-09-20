package com.example.petner.domain.chat.repository;

import com.example.petner.domain.chat.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * 채팅방 ID로 가장 최근 메시지 조회
     * 채팅방 목록에서 마지막 메시지 표시용
     *
     * @param chatRoomId 채팅방 ID
     * @return 가장 최근 메시지 (Optional)
     */
    Optional<Message> findTopByChatRoom_ChatRoomIdOrderBySentAtDesc(Long chatRoomId);

    /**
     * 채팅방 ID로 메시지 조회 (시간순 정렬, 페이징 지원)
     * ERD Messages 테이블의 chatRoomId(FK) 기준 조회
     *
     * @param chatRoomId 채팅방 ID
     * @param pageable 페이징 정보
     * @return 해당 채팅방의 메시지 목록
     */
    List<Message> findByChatRoom_ChatRoomId(Long chatRoomId, Pageable pageable);

    /**
     * 채팅방 ID로 모든 메시지 조회 (시간순 정렬)
     * ERD Messages 테이블의 chatRoomId(FK) 기준 조회
     *
     * @param chatRoomId 채팅방 ID
     * @return 해당 채팅방의 모든 메시지 목록 (오래된 순)
     */
    List<Message> findByChatRoom_ChatRoomIdOrderBySentAtAsc(Long chatRoomId);

    /**
     * 여러 채팅방의 마지막 메시지를 한 번에 조회 (N+1 문제 해결)
     * Window Function을 사용하여 각 채팅방별 최근 메시지 1개씩만 조회
     *
     * @param chatRoomIds 조회할 채팅방 ID 리스트
     * @return 각 채팅방의 마지막 메시지 리스트
     */
    @Query(value = """
        SELECT m.* FROM messages m
        INNER JOIN (
            SELECT chat_room_id, MAX(sent_at) as max_sent_at
            FROM messages
            WHERE chat_room_id IN :chatRoomIds
            GROUP BY chat_room_id
        ) latest ON m.chat_room_id = latest.chat_room_id AND m.sent_at = latest.max_sent_at
        """, nativeQuery = true)
    List<Message> findLastMessagesByChatRoomIds(@Param("chatRoomIds") List<Long> chatRoomIds);
}