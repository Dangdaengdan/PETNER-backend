package com.example.petner.domain.chat.dto.response;

import com.example.petner.domain.chat.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 채팅 메시지 응답 DTO
 *
 * 책임:
 * - ERD Messages 테이블 컬럼에 맞춘 응답 데이터 캡슐화
 * - WebSocket 실시간 메시지 및 REST API 응답 모두 지원
 * - 불변성 보장을 통한 데이터 무결성 유지 (Immutable Object Pattern)
 *
 * ERD Messages 테이블 매핑:
 * - messageId → Messages.messageId (PK)
 * - senderId → Messages.senderId (FK from Members)
 * - content → Messages.content
 * - sendAt → Messages.sendAt
 *
 * @author VIBE CODING Team
 */
@Getter
@AllArgsConstructor
public class ChatMessageResponseDto {

    /**
     * 메시지 고유 식별자
     * ERD Messages.messageId (PK)
     */
    private Long messageId;

    /**
     * 메시지 발신자 ID
     * ERD Messages.senderId (FK from Members)
     */
    private Long senderId;

    /**
     * 메시지 내용
     * ERD Messages.content
     */
    private String content;

    /**
     * 메시지 전송 시간
     * ERD Messages.sendAt
     */
    private LocalDateTime sendAt;

    /**
     * Message 엔티티로부터 DTO 생성
     *
     * @param message 변환할 Message 엔티티
     */
    public ChatMessageResponseDto(Message message) {
        this.messageId = message.getMessageId();
        this.senderId = message.getSender().getMemberId();
        this.content = message.getContent();
        this.sendAt = message.getSentAt();
    }
}